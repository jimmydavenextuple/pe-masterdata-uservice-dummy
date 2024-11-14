package com.nextuple.common.userexit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class UserExitClassFinderDefaultImplTest {
  @InjectMocks UserExitClassFinderDefaultImpl userExitClassFinderDefault;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void findUserExitClassNameForTest() {
    String userExitClass =
        userExitClassFinderDefault.findUserExitClassNameFor("package", "tenant", "userExitName");
    Assertions.assertEquals("package.userExitNametenant", userExitClass);
  }
}
