TCK Results
===========

As required by the
[Eclipse Foundation Technology Compatibility Kit License](https://www.eclipse.org/legal/tck.php),
following is a summary of the TCK results for releases of Jakarta Authentication.

# 1.1 Certification Request

- [x] Organization Name ("Organization") and, if applicable, URL
  Eclipse Foundation
- [x] Product Name, Version and download URL (if applicable)
  [Eclipse GlassFish 5.1](https://eclipse-ee4j.github.io/glassfish)
- [x] Specification Name, Version and download URL
  [Jakarta Authentication 1.1](https://jakarta.ee/specifications/authentication/1.1/)
- [x] TCK Version, digital SHA-256 fingerprint and download URL
  [Jakarta Authentication TCK 1.1.0](http://download.eclipse.org/ee4j/jakartaee-tck/jakartaee8-eftl/staged/eclipse-authentication-tck-1.1.0.zip), SHA-256: 3bd5919a95ad4e6a9e610a585448f90b8b4475a52cc9ebad429b412b949c0a89
- [x] Public URL of TCK Results Summary
  [TCK results summary](https://eclipse-ee4j.github.io/jaspic/certifications/authentication/1.1/TCK-Results)
- [x] Any Additional Specification Certification Requirements
  None
- [x] Java runtime used to run the implementation\
  Oracle JDK 1.8.0_202
- [x] Summary of the information for the certification environment, operating system, cloud, ...
  Linux
- [x] By checking this box I acknowledge that the Organization I represent accepts the terms of the [EFTL](https://www.eclipse.org/legal/tck.php).
- [x] By checking this box I attest that all TCK requirements have been met, including any compatibility rules.



Test results:

```
[javatest.batch] Number of Tests Passed      = 106
[javatest.batch] Number of Tests Failed      = 0
[javatest.batch] Number of Tests with Errors = 0
[javatest.batch] ********************************************************************************
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/authstatus/authexception/Client.java#AuthStatusAuthException
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/authstatus/failure/Client.java#AuthStatusFailure
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/authstatus/sendfailure/Client.java#AuthStatusSendFailure
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/baseline/Client.java#ACF_getFactory_from_jaspicservlet
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/baseline/Client.java#ACFInMemoryRegisterOnlyOneACP_from_jaspicservlet
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/baseline/Client.java#ACFPersistentRegisterOnlyOneACP_from_jaspicservlet
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/baseline/Client.java#ACFRemoveRegistrationWithBadId_from_jaspicservlet
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/baseline/Client.java#ACFSwitchFactorys_from_jaspicservlet
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/baseline/Client.java#ACFUnregisterACP_from_jaspicservlet
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/baseline/Client.java#testACFComesFromSecFile_from_jaspicservlet
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFDetachListener
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFGetFactory
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFGetRegistrationContext
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFGetRegistrationIDs
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFInMemoryNotifyOnUnReg
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFInMemoryPrecedenceRules
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFInMemoryRegisterOnlyOneACP
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFPersistentNotifyOnUnReg
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFPersistentPrecedenceRules
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFPersistentRegisterOnlyOneACP
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFRemoveRegistration
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFRemoveRegistrationWithBadId
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFSwitchFactorys
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#ACFUnregisterACP
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#AuthConfigFactoryGetFactory
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#AuthConfigFactoryRegistration
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#AuthConfigFactorySetFactory
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#AuthConfigFactoryVerifyPersistence
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckACFVerifyPersistence
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckACPConfigObjAppContext
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckACPContextObjAppContext
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckAuthContextId
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckAuthContextIdUsingGet
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckAuthenInValidateRequest
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckCallbackSupport
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckforNonNullAuthContext
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckforNonNullCallback
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckMessageInfo
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckMPRCallsGetAuthContext
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckMsgInfoKey
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckNoAuthReturnsValidStatusCode
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckRegistrationContextId
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#checkSACValidateRequestWithVaryingAccess
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckSecureRespForMandatoryAuth
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckSecureRespForOptionalAuth
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckServletAppContext
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckValidateReqAlwaysCalled
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#CheckValidateReqAuthException
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#GetConfigProvider
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#getRegistrationContextId
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testACFComesFromSecFile
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testAuthenAfterLogout
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testAuthenIsUserInRole
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testAuthenResultsOnHttpServlet
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testGPCGetAuthType
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testGPCGetRemoteUser
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testGPCGetUserPrincipal
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testGPCIsUserInRole
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testGPCWithNoRequiredAuth
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testRemoteUserCorrespondsToPrin
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testRequestWrapper
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testResponseWrapper
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#testSecRespCalledAfterSvcInvoc
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#VerifyClientSubjects
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#VerifyMessageInfoObjects
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#VerifyNoInvalidEntries
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#VerifyReqPolicy
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#VerifyRequestDispatchedProperly
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#verifyRuntimeCallOrder
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#VerifySAContextVerifyReqIsCalled
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/servlet/Client.java#VerifyServiceSubjects
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ACFGetFactory
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ACFInMemoryRegisterOnlyOneACP
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ACFPersistentRegisterOnlyOneACP
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ACFRemoveRegistrationWithBadId
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ACFSwitchFactorys
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ACFUnregisterACP
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ACPAuthContext
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ACPClientAuthConfig
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ACPServerAuthConfig
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#AuthConfigFactoryRegistration
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#AuthConfigFactorySetFactory
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#AuthConfigFactoryVerifyPersistence
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#CACRequestResponse
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ClientAppContextId
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ClientAuthConfig
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ClientAuthContext
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ClientRuntimeCommonCallbackSupport
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ClientRuntimeMessageInfoMap
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#GetConfigProvider
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#GetFactory
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#getRegistrationContextId
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#MessageInfo
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#NameAndPasswordCallbackSupport
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#OperationId
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#SACRequestResponse
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#SecureRequest
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#SecureResponse
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ServerAppContextId
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ServerAuthConfig
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ServerAuthContext
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ServerRuntimeCallbackSupport
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ServerRuntimeCommonCallbackSupport
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#testACFComesFromSecFile
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ValidateRequest
[javatest.batch] PASSED........com/sun/ts/tests/jaspic/spi/soap/Client.java#ValidateResponse
[javatest.batch] 
[javatest.batch] Aug 7, 2019 7:31:19 PM Finished executing all tests, wait for cleanup...
[javatest.batch] Aug 7, 2019 7:31:19 PM Harness done with cleanup from test run.
[javatest.batch] Total time = 449s
[javatest.batch] Setup time = 0s
[javatest.batch] Cleanup time = 3s
[javatest.batch] Test results: passed: 106
[javatest.batch] Results written to /home/jenkins/workspace/2_authentication_run_tck_against_staged_build/JTwork.
[javatest.batch] Report written to /home/jenkins/workspace/2_authentication_run_tck_against_staged_build/JTreport

BUILD SUCCESSFUL
Total time: 7 minutes 32 seconds
```