package com.nextuple.common.userexit;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class UserExitClassFinderDefaultImpl implements UserExitClassFinder {
  @Override
  public String findUserExitClassNameFor(String packageName, String tenantId, String userExitName) {
    return packageName + "." + userExitName + tenantId;
  }
}
