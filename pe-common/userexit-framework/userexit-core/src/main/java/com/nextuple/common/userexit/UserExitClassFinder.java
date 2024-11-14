package com.nextuple.common.userexit;

public interface UserExitClassFinder {
  String findUserExitClassNameFor(String packageName, String tenantId, String userExitName);
}
