#!/bin/bash

echo $ECR_REGISTRY
export SERVER_CONFIG_FILE="./manifests/config.yml"
export REPO=`echo $GITHUB_REPOSITORY | awk -F "/" '{print $2}'`
export SERVICE_NAME="$REPO-$PROJECT"
export VERSION=`bash ./gradlew -Pbuild_target=SNAPSHOT -q properties -p $PROJECT | grep version | sed -e "s@version: @@g"`
export SERVICE_CODE=`echo "$VERSION" | cut -d '-' -f1`
export IMAGE_NAME="$ECR_REGISTRY/$REPO-$PROJECT:$VERSION.$COMMIT_HASH"

echo "$IMAGE_NAME"

aws sts get-caller-identity

if [ "$ENVIRONMENT" == "dev" -o "$ENVIRONMENT" == "qa" ]; then
  export AWS_EKS_NAME="dev-eks-cluster"
elif [ "$ENVIRONMENT" == "stage" ]; then
  export AWS_EKS_NAME="stage-eks-cluster"
elif [ "$ENVIRONMENT" == "perf" ]; then
  export AWS_EKS_NAME="perf-eks-cluster"
fi


aws eks update-kubeconfig --name $AWS_EKS_NAME --region us-east-1


export COLOR1="blue"
export COLOR2="green"

export SERVICE_ACTIVE_COLOR=$(kubectl get svc $SERVICE_NAME -n $ENVIRONMENT -o jsonpath='{.spec.selector.color}' --ignore-not-found=true)

if [ "$SERVICE_ACTIVE_COLOR" == "$COLOR1" ]; then
  export SERVICE_PASSIVE_COLOR="$COLOR2"
else
  export SERVICE_PASSIVE_COLOR="$COLOR1"
fi

echo "Currently running deployment color : $SERVICE_ACTIVE_COLOR"

export HEALTH_CHECK_PATH=`yq -e eval ".server.health-check-endpoint" $SERVER_CONFIG_FILE`
export REPLICAS=`yq -e eval ".server.environments.$ENVIRONMENT.replicas" $SERVER_CONFIG_FILE`
export MIN_MEM_REQUIRED=`yq -e eval ".server.environments.$ENVIRONMENT.resources.min.memory" $SERVER_CONFIG_FILE`
export MAX_MEM_REQUIRED=`yq -e eval ".server.environments.$ENVIRONMENT.resources.max.memory" $SERVER_CONFIG_FILE`
export MIN_CPU_REQUIRED=`yq -e eval ".server.environments.$ENVIRONMENT.resources.min.cpu" $SERVER_CONFIG_FILE`
export MAX_CPU_REQUIRED=`yq -e eval ".server.environments.$ENVIRONMENT.resources.max.cpu" $SERVER_CONFIG_FILE`

sed -e "s@<SERVICE_VERSION>@$SERVICE_CODE@g" \
    -e "s@<SERVICE_NAME>@$SERVICE_NAME@g" \
    -e "s@<IMAGE_NAME>@$IMAGE_NAME@g" \
    -e "s@<ENVIRONMENT>@$ENVIRONMENT@g" \
    -e "s@<COLOR>@$SERVICE_PASSIVE_COLOR@g" \
    -e "s@<REPLICAS>@$REPLICAS@g" \
    -e "s@<HEALTH_CHECK_PATH>@$HEALTH_CHECK_PATH@g" \
    -e "s@<MIN_MEM>@$MIN_MEM_REQUIRED@g" \
    -e "s@<MAX_MEM>@$MAX_MEM_REQUIRED@g" \
    -e "s@<MIN_CPU>@$MIN_CPU_REQUIRED@g" \
    -e "s@<MAX_CPU>@$MAX_CPU_REQUIRED@g" \
    ./manifests/deployment.yaml | kubectl apply -f -

set +e
kubectl rollout status -w deployment/$SERVICE_NAME-$SERVICE_PASSIVE_COLOR -n=$ENVIRONMENT --timeout=20m
if [ $? -eq 0 ]; then
  set -e
  echo "OK"
else
  set -e
  kubectl delete deployment/$SERVICE_NAME-$SERVICE_PASSIVE_COLOR -n=$ENVIRONMENT --ignore-not-found=true
  ./kub-errornous-command-to-fail
fi

sed -e "s@<SERVICE_VERSION>@$SERVICE_CODE@g" \
    -e "s@<SERVICE_NAME>@$SERVICE_NAME@g" \
    -e "s@<ENVIRONMENT>@$ENVIRONMENT@g" \
    -e "s@<COLOR>@$SERVICE_PASSIVE_COLOR@g" \
    ./manifests/service.yaml | kubectl apply -f -

kubectl delete deployment/$SERVICE_NAME-$SERVICE_ACTIVE_COLOR -n=$ENVIRONMENT --ignore-not-found=true

