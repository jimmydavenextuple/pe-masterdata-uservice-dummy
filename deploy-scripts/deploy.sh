#!/bin/bash

echo $ECR_REGISTRY
export SERVER_CONFIG_FILE="./manifests/config.yml"
export BUILD_TARGET="SNAPSHOT"

NAMESPACE=$ENVIRONMENT
ENVIRONMENT=`echo $ENVIRONMENT | sed 's/[0-9]\+$//'`

aws sts get-caller-identity

if [ "$ENVIRONMENT" == "dev" -o "$ENVIRONMENT" == "qa" ]; then
  export AWS_EKS_NAME="eks-cluster-promise-development"
  export ENV_TAG="development"
elif [ "$ENVIRONMENT" == "stage" ]; then
  export AWS_EKS_NAME="eks-cluster-promise-stage"
  export ENV_TAG="stage"
elif [ "$ENVIRONMENT" == "perf" ]; then
  export AWS_EKS_NAME="eks-cluster-promise-performance"
  export ENV_TAG="stage"
elif [ "$ENVIRONMENT" == "hotfix" ]; then
  export AWS_EKS_NAME="eks-cluster-promise-development"
  export ENV_TAG="development"
  export BUILD_TARGET="SNAPSHOT"
elif [ "$ENVIRONMENT" == "prod" ]; then
  export AWS_EKS_NAME="eks-cluster-promise-production"
  export ENV_TAG="production"
fi

aws eks update-kubeconfig --name $AWS_EKS_NAME --region us-east-1

if [ -e "./$PROJECT/config.yml" ]; then
  SERVER_CONFIG_FILE="./$PROJECT/config.yml"
fi

export REPO=`echo $GITHUB_REPOSITORY | awk -F "/" '{print $2}'`
export SERVICE_NAME="$REPO-$PROJECT"

if [ -z "$TAG" ]; then
    export VERSION=`bash ./gradlew -Pbuild_target=$BUILD_TARGET -q properties -p $PROJECT | grep version | sed -e "s@version: @@g"`
else
    export VERSION=$(echo $TAG | cut -d "v" -f2)
    echo "Received version from git tag : ${VERSION}"
fi

export SERVICE_CODE=`echo "$VERSION" | cut -d '-' -f1`
export BUILD_IMAGE_NAME=$ECR_REGISTRY/$REPO-$PROJECT:$SERVICE_CODE.$COMMIT_HASH
export IMAGE_NAME=$ECR_REGISTRY/$REPO-$PROJECT:$VERSION.$NAMESPACE
docker pull $BUILD_IMAGE_NAME
docker tag $BUILD_IMAGE_NAME $IMAGE_NAME
docker push $IMAGE_NAME

echo "$IMAGE_NAME"


export NLB_NAME=`echo "tf-$NAMESPACE-$PROJECT" | cut -c1-31`
if [ "${NLB_NAME: -1}" == "-" ]; then
    NLB_NAME=`echo "$NLB_NAME" | sed 's/.$//'`
    NLB_NAME="tf-$NLB_NAME"
fi


export COLOR1="blue"
export COLOR2="green"

export SERVICE_ACTIVE_COLOR=$(kubectl get svc $SERVICE_NAME -n $NAMESPACE -o jsonpath='{.spec.selector.color}' --ignore-not-found=true)

if [ "$SERVICE_ACTIVE_COLOR" == "$COLOR1" ]; then
  export SERVICE_PASSIVE_COLOR="$COLOR2"
else
  export SERVICE_PASSIVE_COLOR="$COLOR1"
fi

echo "Currently running deployment color : $SERVICE_ACTIVE_COLOR"

export HEALTH_CHECK_PATH=`yq -e eval ".server.health-check-endpoint" $SERVER_CONFIG_FILE`
export NLB_NEEDED=`yq -e eval ".server.environments.$ENVIRONMENT.nlb" $SERVER_CONFIG_FILE`
export CUSTOM_JAVA_OPTS=`yq -e eval ".server.environments.$ENVIRONMENT.java-opts" $SERVER_CONFIG_FILE`
export REPLICAS=`yq -e eval ".server.environments.$ENVIRONMENT.replicas" $SERVER_CONFIG_FILE`
export MIN_MEM_REQUIRED=`yq -e eval ".server.environments.$ENVIRONMENT.resources.min.memory" $SERVER_CONFIG_FILE`
export MAX_MEM_REQUIRED=`yq -e eval ".server.environments.$ENVIRONMENT.resources.max.memory" $SERVER_CONFIG_FILE`
export MIN_CPU_REQUIRED=`yq -e eval ".server.environments.$ENVIRONMENT.resources.min.cpu" $SERVER_CONFIG_FILE`
export MAX_CPU_REQUIRED=`yq -e eval ".server.environments.$ENVIRONMENT.resources.max.cpu" $SERVER_CONFIG_FILE`

if [ "$CUSTOM_JAVA_OPTS" = 'null' ]; then
  export REPLACE_JAVA_OPTS_COMMENT_WITH="#JAVA_OPTS"
else
  export REPLACE_JAVA_OPTS_COMMENT_WITH=""
fi

if [ "$NLB_NEEDED" = 'null' ]; then
  export REPLACE_NLB_COMMENT_WITH=""
else
  export REPLACE_NLB_COMMENT_WITH="#NLB"
fi

sed -e "s@<SERVICE_VERSION>@$SERVICE_CODE@g" \
    -e "s@<SERVICE_NAME>@$SERVICE_NAME@g" \
    -e "s@<IMAGE_NAME>@$IMAGE_NAME@g" \
    -e "s@<ENVIRONMENT>@$ENVIRONMENT@g" \
    -e "s@<NAMESPACE>@$NAMESPACE@g" \
    -e "s@<CLUSTER_NAME>@$AWS_EKS_NAME@g" \
    -e "s@<NEW_RELIC_LICENSE_KEY>@$NEW_RELIC_LICENSE_KEY@g" \
    -e "s@<COLOR>@$SERVICE_PASSIVE_COLOR@g" \
    -e "s@<REPLICAS>@$REPLICAS@g" \
    -e "s@<CUSTOM_JAVA_OPTS>@$CUSTOM_JAVA_OPTS@g" \
    -e "s@#JAVA_OPTS@$REPLACE_JAVA_OPTS_COMMENT_WITH@g" \
    -e "s@<HEALTH_CHECK_PATH>@$HEALTH_CHECK_PATH@g" \
    -e "s@<MIN_MEM>@$MIN_MEM_REQUIRED@g" \
    -e "s@<MAX_MEM>@$MAX_MEM_REQUIRED@g" \
    -e "s@<MIN_CPU>@$MIN_CPU_REQUIRED@g" \
    -e "s@<MAX_CPU>@$MAX_CPU_REQUIRED@g" \
    ./manifests/deployment.yaml | kubectl apply -f -

set +e
kubectl rollout status -w deployment/$SERVICE_NAME-$SERVICE_PASSIVE_COLOR -n=$NAMESPACE --timeout=20m
if [ $? -eq 0 ]; then
  set -e
  echo "OK"
else
  set -e
  kubectl delete deployment/$SERVICE_NAME-$SERVICE_PASSIVE_COLOR -n=$NAMESPACE --ignore-not-found=true
  ./kub-errornous-command-to-fail
fi

sed -e "s@<SERVICE_VERSION>@$SERVICE_CODE@g" \
    -e "s@<SERVICE_NAME>@$SERVICE_NAME@g" \
    -e "s@<ENVIRONMENT>@$ENVIRONMENT@g" \
    -e "s@<NAMESPACE>@$NAMESPACE@g" \
    -e "s@#NLB@$REPLACE_NLB_COMMENT_WITH@g" \
    -e "s@<NLB_NAME>@$NLB_NAME@g" \
    -e "s@<ENV_TAG>@$ENV_TAG@g" \
    -e "s@<COLOR>@$SERVICE_PASSIVE_COLOR@g" \
    ./manifests/service.yaml | kubectl apply -f -

kubectl delete deployment/$SERVICE_NAME-$SERVICE_ACTIVE_COLOR -n=$NAMESPACE --ignore-not-found=true

