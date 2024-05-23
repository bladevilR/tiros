/**
 * UUVGetStub.java
 *
 * <p>This file was auto-generated from WSDL by the Apache Axis2 version: 1.8.2 Built on : Jul 13,
 * 2022 (06:38:03 EDT)
 */
package org.jeecg.modules.tiros.uuv.client;

/*
 *  UUVGetStub java implementation
 */

public class UUVGetStub extends org.apache.axis2.client.Stub {
  protected org.apache.axis2.description.AxisOperation[] _operations;

  // hashmaps to keep the fault mapping
  private java.util.Map<org.apache.axis2.client.FaultMapKey, String>
      faultExceptionNameMap =
          new java.util.HashMap<org.apache.axis2.client.FaultMapKey, String>();
  private java.util.Map<org.apache.axis2.client.FaultMapKey, String>
      faultExceptionClassNameMap =
          new java.util.HashMap<org.apache.axis2.client.FaultMapKey, String>();
  private java.util.Map<org.apache.axis2.client.FaultMapKey, String> faultMessageMap =
      new java.util.HashMap<org.apache.axis2.client.FaultMapKey, String>();

  private static int counter = 0;

  private static synchronized String getUniqueSuffix() {
    // reset the counter if it is greater than 99999
    if (counter > 99999) {
      counter = 0;
    }
    counter = counter + 1;
    return Long.toString(System.currentTimeMillis()) + "_" + counter;
  }

  private void populateAxisService() throws org.apache.axis2.AxisFault {

    // creating the Service with a unique name
    _service = new org.apache.axis2.description.AxisService("UUVGet" + getUniqueSuffix());
    addAnonymousOperations();

    // creating the operations
    org.apache.axis2.description.AxisOperation __operation;

    _operations = new org.apache.axis2.description.AxisOperation[2];

    __operation = new org.apache.axis2.description.OutInAxisOperation();

    __operation.setName(new javax.xml.namespace.QName("http://tempuri.org/", "getUserList"));
    _service.addOperation(__operation);

    _operations[0] = __operation;

    __operation = new org.apache.axis2.description.OutInAxisOperation();

    __operation.setName(new javax.xml.namespace.QName("http://tempuri.org/", "getDepartMentList"));
    _service.addOperation(__operation);

    _operations[1] = __operation;
  }

  // populates the faults
  private void populateFaults() {}

  /** Constructor that takes in a configContext */
  public UUVGetStub(
      org.apache.axis2.context.ConfigurationContext configurationContext,
      String targetEndpoint)
      throws org.apache.axis2.AxisFault {
    this(configurationContext, targetEndpoint, false);
  }

  /** Constructor that takes in a configContext and useseperate listner */
  public UUVGetStub(
      org.apache.axis2.context.ConfigurationContext configurationContext,
      String targetEndpoint,
      boolean useSeparateListener)
      throws org.apache.axis2.AxisFault {
    // To populate AxisService
    populateAxisService();
    populateFaults();

    _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, _service);

    _serviceClient
        .getOptions()
        .setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
    _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);

    // Set the soap version
    _serviceClient
        .getOptions()
        .setSoapVersionURI(org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
  }

  /** Default Constructor */
  public UUVGetStub(org.apache.axis2.context.ConfigurationContext configurationContext)
      throws org.apache.axis2.AxisFault {

    this(
        configurationContext,
        "http://apisix.sz-mtr.com:9080/apigw/platform_zNgV39Ec/Services/UUVGet.asmx");
  }

  /** Default Constructor */
  public UUVGetStub() throws org.apache.axis2.AxisFault {

    this("http://apisix.sz-mtr.com:9080/apigw/platform_zNgV39Ec/Services/UUVGet.asmx");
  }

  /** Constructor taking the target endpoint */
  public UUVGetStub(String targetEndpoint) throws org.apache.axis2.AxisFault {
    this(null, targetEndpoint);
  }

  /**
   * Auto generated method signature
   *
   * @see org.jeecg.modules.tiros.uuv.client.UUVGetStubStub#getUserList
   * @param getUserList1
   * @param eSBSecurityToken2
   */
  public GetUserListResponse getUserList(
      GetUserList getUserList1,
      ESBSecurityTokenE eSBSecurityToken2)
      throws java.rmi.RemoteException {

    org.apache.axis2.context.MessageContext _messageContext =
        new org.apache.axis2.context.MessageContext();
    try {
      org.apache.axis2.client.OperationClient _operationClient =
          _serviceClient.createClient(_operations[0].getName());
      _operationClient.getOptions().setAction("http://tempuri.org/GetUserList");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

      addPropertyToOperationClient(
          _operationClient,
          org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
          "&");

      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;

      env =
          toEnvelope(
              getFactory(_operationClient.getOptions().getSoapVersionURI()),
              getUserList1,
              optimizeContent(new javax.xml.namespace.QName("http://tempuri.org/", "getUserList")),
              new javax.xml.namespace.QName("http://tempuri.org/", "GetUserList"));

      // add the children only if the parameter is not null
      if (eSBSecurityToken2 != null) {

        org.apache.axiom.om.OMElement omElementeSBSecurityToken2 =
            toOM(
                eSBSecurityToken2,
                optimizeContent(
                    new javax.xml.namespace.QName("http://tempuri.org/", "getUserList")));
        addHeader(omElementeSBSecurityToken2, env);
      }

      // adding SOAP soap_headers
      _serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);

      org.apache.axis2.context.MessageContext _returnMessageContext =
          _operationClient.getMessageContext(
              org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
      org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
      _returnEnv.buildWithAttachments();

      Object object =
          fromOM(
              _returnEnv.getBody().getFirstElement(),
              GetUserListResponse.class);
      org.apache.axis2.kernel.TransportUtils.detachInputStream(_returnMessageContext);

      return (GetUserListResponse) object;

    } catch (org.apache.axis2.AxisFault f) {

      org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (faultExceptionNameMap.containsKey(
            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "GetUserList"))) {
          // make the fault by reflection
          try {
            String exceptionClassName =
                faultExceptionClassNameMap.get(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "GetUserList"));
            Class exceptionClass = Class.forName(exceptionClassName);
            java.lang.reflect.Constructor constructor =
                exceptionClass.getConstructor(String.class);
            Exception ex = (Exception) constructor.newInstance(f.getMessage());
            // message class
            String messageClassName =
                faultMessageMap.get(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "GetUserList"));
            Class messageClass = Class.forName(messageClassName);
            Object messageObject = fromOM(faultElt, messageClass);
            java.lang.reflect.Method m =
                exceptionClass.getMethod("setFaultMessage", new Class[] {messageClass});
            m.invoke(ex, new Object[] {messageObject});

            throw new java.rmi.RemoteException(ex.getMessage(), ex);
          } catch (ClassCastException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (ClassNotFoundException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (NoSuchMethodException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (java.lang.reflect.InvocationTargetException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (IllegalAccessException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (InstantiationException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          }
        } else {
          throw f;
        }
      } else {
        throw f;
      }
    } finally {
      if (_messageContext.getTransportOut() != null) {
        _messageContext.getTransportOut().getSender().cleanup(_messageContext);
      }
    }
  }

  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @see org.jeecg.modules.tiros.uuv.client.UUVGetStub#startgetUserList
   * @param getUserList1
   * @param eSBSecurityToken2
   */
  public void startgetUserList(
      GetUserList getUserList1,
      ESBSecurityTokenE eSBSecurityToken2,
      final UUVGetCallbackHandler callback)
      throws java.rmi.RemoteException {

    org.apache.axis2.client.OperationClient _operationClient =
        _serviceClient.createClient(_operations[0].getName());
    _operationClient.getOptions().setAction("http://tempuri.org/GetUserList");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

    addPropertyToOperationClient(
        _operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");

    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext =
        new org.apache.axis2.context.MessageContext();

    // Style is Doc.

    env =
        toEnvelope(
            getFactory(_operationClient.getOptions().getSoapVersionURI()),
            getUserList1,
            optimizeContent(new javax.xml.namespace.QName("http://tempuri.org/", "getUserList")),
            new javax.xml.namespace.QName("http://tempuri.org/", "GetUserList"));

    // add the soap_headers only if they are not null
    if (eSBSecurityToken2 != null) {

      org.apache.axiom.om.OMElement omElementeSBSecurityToken2 =
          toOM(
              eSBSecurityToken2,
              optimizeContent(new javax.xml.namespace.QName("http://tempuri.org/", "getUserList")));
      addHeader(omElementeSBSecurityToken2, env);
    }

    // adding SOAP soap_headers
    _serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);

    _operationClient.setCallback(
        new org.apache.axis2.client.async.AxisCallback() {
          public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
            try {
              org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

              Object object =
                  fromOM(
                      resultEnv.getBody().getFirstElement(),
                      GetUserListResponse.class);
              callback.receiveResultgetUserList(
                  (GetUserListResponse) object);

            } catch (org.apache.axis2.AxisFault e) {
              callback.receiveErrorgetUserList(e);
            }
          }

          public void onError(Exception error) {
            if (error instanceof org.apache.axis2.AxisFault) {
              org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
              org.apache.axiom.om.OMElement faultElt = f.getDetail();
              if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "GetUserList"))) {
                  // make the fault by reflection
                  try {
                    String exceptionClassName =
                        faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "GetUserList"));
                    Class exceptionClass = Class.forName(exceptionClassName);
                    java.lang.reflect.Constructor constructor =
                        exceptionClass.getConstructor(String.class);
                    Exception ex =
                        (Exception) constructor.newInstance(f.getMessage());
                    // message class
                    String messageClassName =
                        faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "GetUserList"));
                    Class messageClass = Class.forName(messageClassName);
                    Object messageObject = fromOM(faultElt, messageClass);
                    java.lang.reflect.Method m =
                        exceptionClass.getMethod(
                            "setFaultMessage", new Class[] {messageClass});
                    m.invoke(ex, new Object[] {messageObject});

                    callback.receiveErrorgetUserList(
                        new java.rmi.RemoteException(ex.getMessage(), ex));
                  } catch (ClassCastException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetUserList(f);
                  } catch (ClassNotFoundException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetUserList(f);
                  } catch (NoSuchMethodException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetUserList(f);
                  } catch (java.lang.reflect.InvocationTargetException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetUserList(f);
                  } catch (IllegalAccessException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetUserList(f);
                  } catch (InstantiationException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetUserList(f);
                  } catch (org.apache.axis2.AxisFault e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetUserList(f);
                  }
                } else {
                  callback.receiveErrorgetUserList(f);
                }
              } else {
                callback.receiveErrorgetUserList(f);
              }
            } else {
              callback.receiveErrorgetUserList(error);
            }
          }

          public void onFault(org.apache.axis2.context.MessageContext faultContext) {
            org.apache.axis2.AxisFault fault =
                org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
            onError(fault);
          }

          public void onComplete() {
            try {
              _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            } catch (org.apache.axis2.AxisFault axisFault) {
              callback.receiveErrorgetUserList(axisFault);
            }
          }
        });

    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (_operations[0].getMessageReceiver() == null
        && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      _operations[0].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);
  }

  /**
   * Auto generated method signature
   *
   * @see org.jeecg.modules.tiros.uuv.client.UUVGetStub#getDepartMentList
   * @param getDepartMentList4
   * @param eSBSecurityToken5
   */
  public GetDepartMentListResponse getDepartMentList(
      GetDepartMentList getDepartMentList4,
      ESBSecurityTokenE eSBSecurityToken5)
      throws java.rmi.RemoteException {

    org.apache.axis2.context.MessageContext _messageContext =
        new org.apache.axis2.context.MessageContext();
    try {
      org.apache.axis2.client.OperationClient _operationClient =
          _serviceClient.createClient(_operations[1].getName());
      _operationClient.getOptions().setAction("http://tempuri.org/GetDepartMentList");
      _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

      addPropertyToOperationClient(
          _operationClient,
          org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
          "&");

      // create SOAP envelope with that payload
      org.apache.axiom.soap.SOAPEnvelope env = null;

      env =
          toEnvelope(
              getFactory(_operationClient.getOptions().getSoapVersionURI()),
              getDepartMentList4,
              optimizeContent(
                  new javax.xml.namespace.QName("http://tempuri.org/", "getDepartMentList")),
              new javax.xml.namespace.QName("http://tempuri.org/", "GetDepartMentList"));

      // add the children only if the parameter is not null
      if (eSBSecurityToken5 != null) {

        org.apache.axiom.om.OMElement omElementeSBSecurityToken5 =
            toOM(
                eSBSecurityToken5,
                optimizeContent(
                    new javax.xml.namespace.QName("http://tempuri.org/", "getDepartMentList")));
        addHeader(omElementeSBSecurityToken5, env);
      }

      // adding SOAP soap_headers
      _serviceClient.addHeadersToEnvelope(env);
      // set the message context with that soap envelope
      _messageContext.setEnvelope(env);

      // add the message contxt to the operation client
      _operationClient.addMessageContext(_messageContext);

      // execute the operation client
      _operationClient.execute(true);

      org.apache.axis2.context.MessageContext _returnMessageContext =
          _operationClient.getMessageContext(
              org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
      org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
      _returnEnv.buildWithAttachments();

      Object object =
          fromOM(
              _returnEnv.getBody().getFirstElement(),
              GetDepartMentListResponse.class);
      org.apache.axis2.kernel.TransportUtils.detachInputStream(_returnMessageContext);

      return (GetDepartMentListResponse) object;

    } catch (org.apache.axis2.AxisFault f) {

      org.apache.axiom.om.OMElement faultElt = f.getDetail();
      if (faultElt != null) {
        if (faultExceptionNameMap.containsKey(
            new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "GetDepartMentList"))) {
          // make the fault by reflection
          try {
            String exceptionClassName =
                faultExceptionClassNameMap.get(
                    new org.apache.axis2.client.FaultMapKey(
                        faultElt.getQName(), "GetDepartMentList"));
            Class exceptionClass = Class.forName(exceptionClassName);
            java.lang.reflect.Constructor constructor =
                exceptionClass.getConstructor(String.class);
            Exception ex = (Exception) constructor.newInstance(f.getMessage());
            // message class
            String messageClassName =
                faultMessageMap.get(
                    new org.apache.axis2.client.FaultMapKey(
                        faultElt.getQName(), "GetDepartMentList"));
            Class messageClass = Class.forName(messageClassName);
            Object messageObject = fromOM(faultElt, messageClass);
            java.lang.reflect.Method m =
                exceptionClass.getMethod("setFaultMessage", new Class[] {messageClass});
            m.invoke(ex, new Object[] {messageObject});

            throw new java.rmi.RemoteException(ex.getMessage(), ex);
          } catch (ClassCastException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (ClassNotFoundException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (NoSuchMethodException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (java.lang.reflect.InvocationTargetException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (IllegalAccessException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          } catch (InstantiationException e) {
            // we cannot intantiate the class - throw the original Axis fault
            throw f;
          }
        } else {
          throw f;
        }
      } else {
        throw f;
      }
    } finally {
      if (_messageContext.getTransportOut() != null) {
        _messageContext.getTransportOut().getSender().cleanup(_messageContext);
      }
    }
  }

  /**
   * Auto generated method signature for Asynchronous Invocations
   *
   * @see org.jeecg.modules.tiros.uuv.client.UUVGetStub#startgetDepartMentList
   * @param getDepartMentList4
   * @param eSBSecurityToken5
   */
  public void startgetDepartMentList(
      GetDepartMentList getDepartMentList4,
      ESBSecurityTokenE eSBSecurityToken5,
      final UUVGetCallbackHandler callback)
      throws java.rmi.RemoteException {

    org.apache.axis2.client.OperationClient _operationClient =
        _serviceClient.createClient(_operations[1].getName());
    _operationClient.getOptions().setAction("http://tempuri.org/GetDepartMentList");
    _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

    addPropertyToOperationClient(
        _operationClient,
        org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
        "&");

    // create SOAP envelope with that payload
    org.apache.axiom.soap.SOAPEnvelope env = null;
    final org.apache.axis2.context.MessageContext _messageContext =
        new org.apache.axis2.context.MessageContext();

    // Style is Doc.

    env =
        toEnvelope(
            getFactory(_operationClient.getOptions().getSoapVersionURI()),
            getDepartMentList4,
            optimizeContent(
                new javax.xml.namespace.QName("http://tempuri.org/", "getDepartMentList")),
            new javax.xml.namespace.QName("http://tempuri.org/", "GetDepartMentList"));

    // add the soap_headers only if they are not null
    if (eSBSecurityToken5 != null) {

      org.apache.axiom.om.OMElement omElementeSBSecurityToken5 =
          toOM(
              eSBSecurityToken5,
              optimizeContent(
                  new javax.xml.namespace.QName("http://tempuri.org/", "getDepartMentList")));
      addHeader(omElementeSBSecurityToken5, env);
    }

    // adding SOAP soap_headers
    _serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);

    _operationClient.setCallback(
        new org.apache.axis2.client.async.AxisCallback() {
          public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
            try {
              org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

              Object object =
                  fromOM(
                      resultEnv.getBody().getFirstElement(),
                      GetDepartMentListResponse
                          .class);
              callback.receiveResultgetDepartMentList(
                  (GetDepartMentListResponse) object);

            } catch (org.apache.axis2.AxisFault e) {
              callback.receiveErrorgetDepartMentList(e);
            }
          }

          public void onError(Exception error) {
            if (error instanceof org.apache.axis2.AxisFault) {
              org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
              org.apache.axiom.om.OMElement faultElt = f.getDetail();
              if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                    new org.apache.axis2.client.FaultMapKey(
                        faultElt.getQName(), "GetDepartMentList"))) {
                  // make the fault by reflection
                  try {
                    String exceptionClassName =
                        faultExceptionClassNameMap.get(
                            new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "GetDepartMentList"));
                    Class exceptionClass = Class.forName(exceptionClassName);
                    java.lang.reflect.Constructor constructor =
                        exceptionClass.getConstructor(String.class);
                    Exception ex =
                        (Exception) constructor.newInstance(f.getMessage());
                    // message class
                    String messageClassName =
                        faultMessageMap.get(
                            new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "GetDepartMentList"));
                    Class messageClass = Class.forName(messageClassName);
                    Object messageObject = fromOM(faultElt, messageClass);
                    java.lang.reflect.Method m =
                        exceptionClass.getMethod(
                            "setFaultMessage", new Class[] {messageClass});
                    m.invoke(ex, new Object[] {messageObject});

                    callback.receiveErrorgetDepartMentList(
                        new java.rmi.RemoteException(ex.getMessage(), ex));
                  } catch (ClassCastException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetDepartMentList(f);
                  } catch (ClassNotFoundException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetDepartMentList(f);
                  } catch (NoSuchMethodException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetDepartMentList(f);
                  } catch (java.lang.reflect.InvocationTargetException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetDepartMentList(f);
                  } catch (IllegalAccessException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetDepartMentList(f);
                  } catch (InstantiationException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetDepartMentList(f);
                  } catch (org.apache.axis2.AxisFault e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    callback.receiveErrorgetDepartMentList(f);
                  }
                } else {
                  callback.receiveErrorgetDepartMentList(f);
                }
              } else {
                callback.receiveErrorgetDepartMentList(f);
              }
            } else {
              callback.receiveErrorgetDepartMentList(error);
            }
          }

          public void onFault(org.apache.axis2.context.MessageContext faultContext) {
            org.apache.axis2.AxisFault fault =
                org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
            onError(fault);
          }

          public void onComplete() {
            try {
              _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            } catch (org.apache.axis2.AxisFault axisFault) {
              callback.receiveErrorgetDepartMentList(axisFault);
            }
          }
        });

    org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if (_operations[1].getMessageReceiver() == null
        && _operationClient.getOptions().isUseSeparateListener()) {
      _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      _operations[1].setMessageReceiver(_callbackReceiver);
    }

    // execute the operation client
    _operationClient.execute(false);
  }

  private javax.xml.namespace.QName[] opNameArray = null;

  private boolean optimizeContent(javax.xml.namespace.QName opName) {

    if (opNameArray == null) {
      return false;
    }
    for (int i = 0; i < opNameArray.length; i++) {
      if (opName.equals(opNameArray[i])) {
        return true;
      }
    }
    return false;
  }
  // http://apisix.sz-mtr.com:9080/apigw/platform_zNgV39Ec/Services/UUVGet.asmx
  public static class GetUserList implements org.apache.axis2.databinding.ADBBean {

    public static final javax.xml.namespace.QName MY_QNAME =
        new javax.xml.namespace.QName("http://tempuri.org/", "GetUserList", "ns1");

    /** field for IsContainPartTimeJob */
    protected boolean localIsContainPartTimeJob;

    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getIsContainPartTimeJob() {
      return localIsContainPartTimeJob;
    }

    /**
     * Auto generated setter method
     *
     * @param param IsContainPartTimeJob
     */
    public void setIsContainPartTimeJob(boolean param) {

      this.localIsContainPartTimeJob = param;
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      String prefix = null;
      String namespace = null;

      prefix = parentQName.getPrefix();
      namespace = parentQName.getNamespaceURI();
      writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

      if (serializeType) {

        String namespacePrefix = registerPrefix(xmlWriter, "http://tempuri.org/");
        if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              namespacePrefix + ":GetUserList",
              xmlWriter);
        } else {
          writeAttribute(
              "xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "GetUserList", xmlWriter);
        }
      }

      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "IsContainPartTimeJob", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException(
            "IsContainPartTimeJob cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                localIsContainPartTimeJob));
      }

      xmlWriter.writeEndElement();

      xmlWriter.writeEndElement();
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://tempuri.org/")) {
        return "ns1";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static GetUserList parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        GetUserList object = new GetUserList();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type")
              != null) {
            String fullTypeName =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (fullTypeName != null) {
              String nsPrefix = null;
              if (fullTypeName.indexOf(":") > -1) {
                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
              }
              nsPrefix = nsPrefix == null ? "" : nsPrefix;

              String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

              if (!"GetUserList".equals(type)) {
                // find namespace for the prefix
                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                return (GetUserList) ExtensionMapper.getTypeObject(nsUri, type, reader);
              }
            }
          }

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          reader.next();

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "IsContainPartTimeJob")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "IsContainPartTimeJob" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setIsContainPartTimeJob(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement())
            // 2 - A start element we are not expecting indicates a trailing invalid property

            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  public static class ArrayOfADUser implements org.apache.axis2.databinding.ADBBean {
    /* This type was generated from the piece of schema that had
    name = ArrayOfADUser
    Namespace URI = http://tempuri.org/
    Namespace Prefix = ns1
    */

    /** field for ADUser This was an Array! */
    protected ADUser[] localADUser;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localADUserTracker = false;

    public boolean isADUserSpecified() {
      return localADUserTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return ADUser[]
     */
    public ADUser[] getADUser() {
      return localADUser;
    }

    /** validate the array for ADUser */
    protected void validateADUser(ADUser[] param) {}

    /**
     * Auto generated setter method
     *
     * @param param ADUser
     */
    public void setADUser(ADUser[] param) {

      validateADUser(param);

      localADUserTracker = true;

      this.localADUser = param;
    }

    /**
     * Auto generated add method for the array for convenience
     *
     * @param param ADUser
     */
    public void addADUser(ADUser param) {
      if (localADUser == null) {
        localADUser = new ADUser[] {};
      }

      // update the setting tracker
      localADUserTracker = true;

      java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localADUser);
      list.add(param);
      this.localADUser = (ADUser[]) list.toArray(new ADUser[list.size()]);
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, parentQName));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      String prefix = null;
      String namespace = null;

      prefix = parentQName.getPrefix();
      namespace = parentQName.getNamespaceURI();
      writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

      if (serializeType) {

        String namespacePrefix = registerPrefix(xmlWriter, "http://tempuri.org/");
        if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              namespacePrefix + ":ArrayOfADUser",
              xmlWriter);
        } else {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              "ArrayOfADUser",
              xmlWriter);
        }
      }
      if (localADUserTracker) {
        if (localADUser != null) {
          for (int i = 0; i < localADUser.length; i++) {
            if (localADUser[i] != null) {
              localADUser[i].serialize(
                  new javax.xml.namespace.QName("http://tempuri.org/", "ADUser"), xmlWriter);
            } else {

              writeStartElement(null, "http://tempuri.org/", "ADUser", xmlWriter);

              // write the nil attribute
              writeAttribute(
                  "xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
              xmlWriter.writeEndElement();
            }
          }
        } else {

          writeStartElement(null, "http://tempuri.org/", "ADUser", xmlWriter);

          // write the nil attribute
          writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
          xmlWriter.writeEndElement();
        }
      }
      xmlWriter.writeEndElement();
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://tempuri.org/")) {
        return "ns1";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static ArrayOfADUser parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        ArrayOfADUser object = new ArrayOfADUser();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type")
              != null) {
            String fullTypeName =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (fullTypeName != null) {
              String nsPrefix = null;
              if (fullTypeName.indexOf(":") > -1) {
                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
              }
              nsPrefix = nsPrefix == null ? "" : nsPrefix;

              String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

              if (!"ArrayOfADUser".equals(type)) {
                // find namespace for the prefix
                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                return (ArrayOfADUser) ExtensionMapper.getTypeObject(nsUri, type, reader);
              }
            }
          }

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          reader.next();

          java.util.ArrayList list1 = new java.util.ArrayList();

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "ADUser")
                  .equals(reader.getName())) {

            // Process the array and step past its final element's end.

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              list1.add(null);
              reader.next();
            } else {
              list1.add(ADUser.Factory.parse(reader));
            }
            // loop until we find a start element that is not part of this array
            boolean loopDone1 = false;
            while (!loopDone1) {
              // We should be at the end element, but make sure
              while (!reader.isEndElement()) reader.next();
              // Step out of this element
              reader.next();
              // Step to next element event.
              while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
              if (reader.isEndElement()) {
                // two continuous end elements means we are exiting the xml structure
                loopDone1 = true;
              } else {
                if (new javax.xml.namespace.QName("http://tempuri.org/", "ADUser")
                    .equals(reader.getName())) {

                  nillableValue =
                      reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                  if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                    list1.add(null);
                    reader.next();
                  } else {
                    list1.add(ADUser.Factory.parse(reader));
                  }
                } else {
                  loopDone1 = true;
                }
              }
            }
            // call the converter utility  to convert and set the array

            object.setADUser(
                (ADUser[])
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                        ADUser.class, list1));

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement())
            // 2 - A start element we are not expecting indicates a trailing invalid property

            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  public static class ESBSecurityToken implements org.apache.axis2.databinding.ADBBean {
    /* This type was generated from the piece of schema that had
    name = ESBSecurityToken
    Namespace URI = http://esb.szmtr.com/wsdl/soapheader/
    Namespace Prefix = ns2
    */

    /** field for Username */
    protected String localUsername;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localUsernameTracker = false;

    public boolean isUsernameSpecified() {
      return localUsernameTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getUsername() {
      return localUsername;
    }

    /**
     * Auto generated setter method
     *
     * @param param Username
     */
    public void setUsername(String param) {
      localUsernameTracker = param != null;

      this.localUsername = param;
    }

    /** field for Password */
    protected String localPassword;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localPasswordTracker = false;

    public boolean isPasswordSpecified() {
      return localPasswordTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getPassword() {
      return localPassword;
    }

    /**
     * Auto generated setter method
     *
     * @param param Password
     */
    public void setPassword(String param) {
      localPasswordTracker = param != null;

      this.localPassword = param;
    }

    /** field for Timestamp */
    protected String localTimestamp;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localTimestampTracker = false;

    public boolean isTimestampSpecified() {
      return localTimestampTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getTimestamp() {
      return localTimestamp;
    }

    /**
     * Auto generated setter method
     *
     * @param param Timestamp
     */
    public void setTimestamp(String param) {
      localTimestampTracker = param != null;

      this.localTimestamp = param;
    }

    /** field for ExtraAttributes This was an Attribute! This was an Array! */
    protected org.apache.axiom.om.OMAttribute[] localExtraAttributes;

    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.om.OMAttribute[]
     */
    public org.apache.axiom.om.OMAttribute[] getExtraAttributes() {
      return localExtraAttributes;
    }

    /** validate the array for ExtraAttributes */
    protected void validateExtraAttributes(org.apache.axiom.om.OMAttribute[] param) {

      if ((param != null) && (param.length > 1)) {
        throw new RuntimeException("Input values do not follow defined XSD restrictions");
      }

      if ((param != null) && (param.length < 1)) {
        throw new RuntimeException("Input values do not follow defined XSD restrictions");
      }
    }

    /**
     * Auto generated setter method
     *
     * @param param ExtraAttributes
     */
    public void setExtraAttributes(org.apache.axiom.om.OMAttribute[] param) {

      validateExtraAttributes(param);

      this.localExtraAttributes = param;
    }

    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.apache.axiom.om.OMAttribute
     */
    public void addExtraAttributes(org.apache.axiom.om.OMAttribute param) {
      if (localExtraAttributes == null) {
        localExtraAttributes = new org.apache.axiom.om.OMAttribute[] {};
      }

      java.util.List list =
          org.apache.axis2.databinding.utils.ConverterUtil.toList(localExtraAttributes);
      list.add(param);
      this.localExtraAttributes =
          (org.apache.axiom.om.OMAttribute[])
              list.toArray(new org.apache.axiom.om.OMAttribute[list.size()]);
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, parentQName));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      String prefix = null;
      String namespace = null;

      prefix = parentQName.getPrefix();
      namespace = parentQName.getNamespaceURI();
      writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

      if (serializeType) {

        String namespacePrefix =
            registerPrefix(xmlWriter, "http://esb.szmtr.com/wsdl/soapheader/");
        if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              namespacePrefix + ":ESBSecurityToken",
              xmlWriter);
        } else {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              "ESBSecurityToken",
              xmlWriter);
        }
      }

      if (localExtraAttributes != null) {
        for (int i = 0; i < localExtraAttributes.length; i++) {
          writeAttribute(
              localExtraAttributes[i].getNamespace().getName(),
              localExtraAttributes[i].getLocalName(),
              localExtraAttributes[i].getAttributeValue(),
              xmlWriter);
        }
      }
      if (localUsernameTracker) {
        namespace = "http://esb.szmtr.com/wsdl/soapheader/";
        writeStartElement(null, namespace, "username", xmlWriter);

        if (localUsername == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("username cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localUsername);
        }

        xmlWriter.writeEndElement();
      }
      if (localPasswordTracker) {
        namespace = "http://esb.szmtr.com/wsdl/soapheader/";
        writeStartElement(null, namespace, "password", xmlWriter);

        if (localPassword == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("password cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localPassword);
        }

        xmlWriter.writeEndElement();
      }
      if (localTimestampTracker) {
        namespace = "http://esb.szmtr.com/wsdl/soapheader/";
        writeStartElement(null, namespace, "timestamp", xmlWriter);

        if (localTimestamp == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("timestamp cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localTimestamp);
        }

        xmlWriter.writeEndElement();
      }
      xmlWriter.writeEndElement();
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://esb.szmtr.com/wsdl/soapheader/")) {
        return "ns2";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static ESBSecurityToken parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        ESBSecurityToken object = new ESBSecurityToken();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type")
              != null) {
            String fullTypeName =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (fullTypeName != null) {
              String nsPrefix = null;
              if (fullTypeName.indexOf(":") > -1) {
                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
              }
              nsPrefix = nsPrefix == null ? "" : nsPrefix;

              String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

              if (!"ESBSecurityToken".equals(type)) {
                // find namespace for the prefix
                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                return (ESBSecurityToken) ExtensionMapper.getTypeObject(nsUri, type, reader);
              }
            }
          }

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          // now run through all any or extra attributes
          // which were not reflected until now
          for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (!handledAttributes.contains(reader.getAttributeLocalName(i))) {
              // this is an anyAttribute and we create
              // an OMAttribute for this
              org.apache.axiom.om.OMFactory factory =
                  org.apache.axiom.om.OMAbstractFactory.getOMFactory();
              org.apache.axiom.om.OMAttribute attr =
                  factory.createOMAttribute(
                      reader.getAttributeLocalName(i),
                      factory.createOMNamespace(
                          reader.getAttributeNamespace(i), reader.getAttributePrefix(i)),
                      reader.getAttributeValue(i));

              // and add it to the extra attributes

              object.addExtraAttributes(attr);
            }
          }

          reader.next();

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://esb.szmtr.com/wsdl/soapheader/", "username")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "username" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setUsername(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://esb.szmtr.com/wsdl/soapheader/", "password")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "password" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPassword(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://esb.szmtr.com/wsdl/soapheader/", "timestamp")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "timestamp" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setTimestamp(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement())
            // 2 - A start element we are not expecting indicates a trailing invalid property

            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  public static class ExtensionMapper {

    public static Object getTypeObject(
        String namespaceURI,
        String typeName,
        javax.xml.stream.XMLStreamReader reader)
        throws Exception {

      if ("http://tempuri.org/".equals(namespaceURI) && "ArrayOfADUser".equals(typeName)) {

        return ArrayOfADUser.Factory.parse(reader);
      }

      if ("http://esb.szmtr.com/wsdl/soapheader/".equals(namespaceURI)
          && "ESBSecurityToken".equals(typeName)) {

        return ESBSecurityToken.Factory.parse(reader);
      }

      if ("http://tempuri.org/".equals(namespaceURI) && "ADDepartment".equals(typeName)) {

        return ADDepartment.Factory.parse(reader);
      }

      if ("http://tempuri.org/".equals(namespaceURI) && "ADUser".equals(typeName)) {

        return ADUser.Factory.parse(reader);
      }

      if ("http://tempuri.org/".equals(namespaceURI) && "ArrayOfADDepartment".equals(typeName)) {

        return ArrayOfADDepartment.Factory.parse(reader);
      }

      throw new org.apache.axis2.databinding.ADBException(
          "Unsupported type " + namespaceURI + " " + typeName);
    }
  }

  public static class GetDepartMentList implements org.apache.axis2.databinding.ADBBean {

    public static final javax.xml.namespace.QName MY_QNAME =
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDepartMentList", "ns1");

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      String prefix = null;
      String namespace = null;

      prefix = parentQName.getPrefix();
      namespace = parentQName.getNamespaceURI();
      writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

      if (serializeType) {

        String namespacePrefix = registerPrefix(xmlWriter, "http://tempuri.org/");
        if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              namespacePrefix + ":GetDepartMentList",
              xmlWriter);
        } else {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              "GetDepartMentList",
              xmlWriter);
        }
      }

      xmlWriter.writeEndElement();
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://tempuri.org/")) {
        return "ns1";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static GetDepartMentList parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        GetDepartMentList object = new GetDepartMentList();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type")
              != null) {
            String fullTypeName =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (fullTypeName != null) {
              String nsPrefix = null;
              if (fullTypeName.indexOf(":") > -1) {
                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
              }
              nsPrefix = nsPrefix == null ? "" : nsPrefix;

              String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

              if (!"GetDepartMentList".equals(type)) {
                // find namespace for the prefix
                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                return (GetDepartMentList) ExtensionMapper.getTypeObject(nsUri, type, reader);
              }
            }
          }

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          reader.next();

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  public static class ESBSecurityTokenE implements org.apache.axis2.databinding.ADBBean {

    public static final javax.xml.namespace.QName MY_QNAME =
        new javax.xml.namespace.QName(
            "http://esb.szmtr.com/wsdl/soapheader/", "ESBSecurityToken", "ns2");

    /** field for ESBSecurityToken */
    protected ESBSecurityToken localESBSecurityToken;

    /**
     * Auto generated getter method
     *
     * @return ESBSecurityToken
     */
    public ESBSecurityToken getESBSecurityToken() {
      return localESBSecurityToken;
    }

    /**
     * Auto generated setter method
     *
     * @param param ESBSecurityToken
     */
    public void setESBSecurityToken(ESBSecurityToken param) {

      this.localESBSecurityToken = param;
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      // We can safely assume an element has only one type associated with it

      if (localESBSecurityToken == null) {
        throw new org.apache.axis2.databinding.ADBException("ESBSecurityToken cannot be null!");
      }
      localESBSecurityToken.serialize(MY_QNAME, xmlWriter);
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://esb.szmtr.com/wsdl/soapheader/")) {
        return "ns2";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static ESBSecurityTokenE parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        ESBSecurityTokenE object = new ESBSecurityTokenE();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          while (!reader.isEndElement()) {
            if (reader.isStartElement()) {

              if (reader.isStartElement()
                  && new javax.xml.namespace.QName(
                          "http://esb.szmtr.com/wsdl/soapheader/", "ESBSecurityToken")
                      .equals(reader.getName())) {

                object.setESBSecurityToken(ESBSecurityToken.Factory.parse(reader));

              } // End of if for expected property start element
              else {
                // 3 - A start element we are not expecting indicates an invalid parameter was
                // passed

                throw new org.apache.axis2.databinding.ADBException(
                    "Unexpected subelement " + reader.getName());
              }

            } else {
              reader.next();
            }
          } // end of while loop

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  public static class GetDepartMentListResponse implements org.apache.axis2.databinding.ADBBean {

    public static final javax.xml.namespace.QName MY_QNAME =
        new javax.xml.namespace.QName("http://tempuri.org/", "GetDepartMentListResponse", "ns1");

    /** field for GetDepartMentListResult */
    protected ArrayOfADDepartment localGetDepartMentListResult;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localGetDepartMentListResultTracker = false;

    public boolean isGetDepartMentListResultSpecified() {
      return localGetDepartMentListResultTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return ArrayOfADDepartment
     */
    public ArrayOfADDepartment getGetDepartMentListResult() {
      return localGetDepartMentListResult;
    }

    /**
     * Auto generated setter method
     *
     * @param param GetDepartMentListResult
     */
    public void setGetDepartMentListResult(ArrayOfADDepartment param) {
      localGetDepartMentListResultTracker = param != null;

      this.localGetDepartMentListResult = param;
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      String prefix = null;
      String namespace = null;

      prefix = parentQName.getPrefix();
      namespace = parentQName.getNamespaceURI();
      writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

      if (serializeType) {

        String namespacePrefix = registerPrefix(xmlWriter, "http://tempuri.org/");
        if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              namespacePrefix + ":GetDepartMentListResponse",
              xmlWriter);
        } else {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              "GetDepartMentListResponse",
              xmlWriter);
        }
      }
      if (localGetDepartMentListResultTracker) {
        if (localGetDepartMentListResult == null) {
          throw new org.apache.axis2.databinding.ADBException(
              "GetDepartMentListResult cannot be null!!");
        }
        localGetDepartMentListResult.serialize(
            new javax.xml.namespace.QName("http://tempuri.org/", "GetDepartMentListResult"),
            xmlWriter);
      }
      xmlWriter.writeEndElement();
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://tempuri.org/")) {
        return "ns1";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static GetDepartMentListResponse parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        GetDepartMentListResponse object = new GetDepartMentListResponse();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type")
              != null) {
            String fullTypeName =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (fullTypeName != null) {
              String nsPrefix = null;
              if (fullTypeName.indexOf(":") > -1) {
                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
              }
              nsPrefix = nsPrefix == null ? "" : nsPrefix;

              String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

              if (!"GetDepartMentListResponse".equals(type)) {
                // find namespace for the prefix
                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                return (GetDepartMentListResponse)
                    ExtensionMapper.getTypeObject(nsUri, type, reader);
              }
            }
          }

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          reader.next();

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "GetDepartMentListResult")
                  .equals(reader.getName())) {

            object.setGetDepartMentListResult(ArrayOfADDepartment.Factory.parse(reader));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement())
            // 2 - A start element we are not expecting indicates a trailing invalid property

            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  public static class ADDepartment implements org.apache.axis2.databinding.ADBBean {
    /* This type was generated from the piece of schema that had
    name = ADDepartment
    Namespace URI = http://tempuri.org/
    Namespace Prefix = ns1
    */

    /** field for Column1 */
    protected String localColumn1;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn1Tracker = false;

    public boolean isColumn1Specified() {
      return localColumn1Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn1() {
      return localColumn1;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column1
     */
    public void setColumn1(String param) {
      localColumn1Tracker = param != null;

      this.localColumn1 = param;
    }

    /** field for Column2 */
    protected String localColumn2;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn2Tracker = false;

    public boolean isColumn2Specified() {
      return localColumn2Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn2() {
      return localColumn2;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column2
     */
    public void setColumn2(String param) {
      localColumn2Tracker = param != null;

      this.localColumn2 = param;
    }

    /** field for Column3 */
    protected String localColumn3;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn3Tracker = false;

    public boolean isColumn3Specified() {
      return localColumn3Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn3() {
      return localColumn3;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column3
     */
    public void setColumn3(String param) {
      localColumn3Tracker = param != null;

      this.localColumn3 = param;
    }

    /** field for Column4 */
    protected String localColumn4;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn4Tracker = false;

    public boolean isColumn4Specified() {
      return localColumn4Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn4() {
      return localColumn4;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column4
     */
    public void setColumn4(String param) {
      localColumn4Tracker = param != null;

      this.localColumn4 = param;
    }

    /** field for Column5 */
    protected String localColumn5;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn5Tracker = false;

    public boolean isColumn5Specified() {
      return localColumn5Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn5() {
      return localColumn5;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column5
     */
    public void setColumn5(String param) {
      localColumn5Tracker = param != null;

      this.localColumn5 = param;
    }

    /** field for CreateTime */
    protected java.util.Calendar localCreateTime;

    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getCreateTime() {
      return localCreateTime;
    }

    /**
     * Auto generated setter method
     *
     * @param param CreateTime
     */
    public void setCreateTime(java.util.Calendar param) {

      this.localCreateTime = param;
    }

    /** field for Creator */
    protected String localCreator;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localCreatorTracker = false;

    public boolean isCreatorSpecified() {
      return localCreatorTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getCreator() {
      return localCreator;
    }

    /**
     * Auto generated setter method
     *
     * @param param Creator
     */
    public void setCreator(String param) {
      localCreatorTracker = param != null;

      this.localCreator = param;
    }

    /** field for DeptCode */
    protected String localDeptCode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDeptCodeTracker = false;

    public boolean isDeptCodeSpecified() {
      return localDeptCodeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getDeptCode() {
      return localDeptCode;
    }

    /**
     * Auto generated setter method
     *
     * @param param DeptCode
     */
    public void setDeptCode(String param) {
      localDeptCodeTracker = param != null;

      this.localDeptCode = param;
    }

    /** field for Deptduty */
    protected String localDeptduty;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDeptdutyTracker = false;

    public boolean isDeptdutySpecified() {
      return localDeptdutyTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getDeptduty() {
      return localDeptduty;
    }

    /**
     * Auto generated setter method
     *
     * @param param Deptduty
     */
    public void setDeptduty(String param) {
      localDeptdutyTracker = param != null;

      this.localDeptduty = param;
    }

    /** field for DeptFullName */
    protected String localDeptFullName;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDeptFullNameTracker = false;

    public boolean isDeptFullNameSpecified() {
      return localDeptFullNameTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getDeptFullName() {
      return localDeptFullName;
    }

    /**
     * Auto generated setter method
     *
     * @param param DeptFullName
     */
    public void setDeptFullName(String param) {
      localDeptFullNameTracker = param != null;

      this.localDeptFullName = param;
    }

    /** field for Line */
    protected String localLine;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localLineTracker = false;

    public boolean isLineSpecified() {
      return localLineTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getLine() {
      return localLine;
    }

    /**
     * Auto generated setter method
     *
     * @param param Line
     */
    public void setLine(String param) {
      localLineTracker = param != null;

      this.localLine = param;
    }

    /** field for DeptID */
    protected String localDeptID;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDeptIDTracker = false;

    public boolean isDeptIDSpecified() {
      return localDeptIDTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getDeptID() {
      return localDeptID;
    }

    /**
     * Auto generated setter method
     *
     * @param param DeptID
     */
    public void setDeptID(String param) {
      localDeptIDTracker = param != null;

      this.localDeptID = param;
    }

    /** field for DeptName */
    protected String localDeptName;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDeptNameTracker = false;

    public boolean isDeptNameSpecified() {
      return localDeptNameTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getDeptName() {
      return localDeptName;
    }

    /**
     * Auto generated setter method
     *
     * @param param DeptName
     */
    public void setDeptName(String param) {
      localDeptNameTracker = param != null;

      this.localDeptName = param;
    }

    /** field for Fax */
    protected String localFax;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localFaxTracker = false;

    public boolean isFaxSpecified() {
      return localFaxTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getFax() {
      return localFax;
    }

    /**
     * Auto generated setter method
     *
     * @param param Fax
     */
    public void setFax(String param) {
      localFaxTracker = param != null;

      this.localFax = param;
    }

    /** field for GroupMailBox */
    protected String localGroupMailBox;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localGroupMailBoxTracker = false;

    public boolean isGroupMailBoxSpecified() {
      return localGroupMailBoxTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getGroupMailBox() {
      return localGroupMailBox;
    }

    /**
     * Auto generated setter method
     *
     * @param param GroupMailBox
     */
    public void setGroupMailBox(String param) {
      localGroupMailBoxTracker = param != null;

      this.localGroupMailBox = param;
    }

    /** field for GroupName */
    protected String localGroupName;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localGroupNameTracker = false;

    public boolean isGroupNameSpecified() {
      return localGroupNameTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getGroupName() {
      return localGroupName;
    }

    /**
     * Auto generated setter method
     *
     * @param param GroupName
     */
    public void setGroupName(String param) {
      localGroupNameTracker = param != null;

      this.localGroupName = param;
    }

    /** field for IsVOrg */
    protected int localIsVOrg;

    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getIsVOrg() {
      return localIsVOrg;
    }

    /**
     * Auto generated setter method
     *
     * @param param IsVOrg
     */
    public void setIsVOrg(int param) {

      this.localIsVOrg = param;
    }

    /** field for Level */
    protected int localLevel;

    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getLevel() {
      return localLevel;
    }

    /**
     * Auto generated setter method
     *
     * @param param Level
     */
    public void setLevel(int param) {

      this.localLevel = param;
    }

    /** field for OrderNo */
    protected int localOrderNo;

    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getOrderNo() {
      return localOrderNo;
    }

    /**
     * Auto generated setter method
     *
     * @param param OrderNo
     */
    public void setOrderNo(int param) {

      this.localOrderNo = param;
    }

    /** field for ParentDeptCode */
    protected String localParentDeptCode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localParentDeptCodeTracker = false;

    public boolean isParentDeptCodeSpecified() {
      return localParentDeptCodeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getParentDeptCode() {
      return localParentDeptCode;
    }

    /**
     * Auto generated setter method
     *
     * @param param ParentDeptCode
     */
    public void setParentDeptCode(String param) {
      localParentDeptCodeTracker = param != null;

      this.localParentDeptCode = param;
    }

    /** field for ParentDeptID */
    protected String localParentDeptID;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localParentDeptIDTracker = false;

    public boolean isParentDeptIDSpecified() {
      return localParentDeptIDTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getParentDeptID() {
      return localParentDeptID;
    }

    /**
     * Auto generated setter method
     *
     * @param param ParentDeptID
     */
    public void setParentDeptID(String param) {
      localParentDeptIDTracker = param != null;

      this.localParentDeptID = param;
    }

    /** field for PosCount */
    protected int localPosCount;

    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getPosCount() {
      return localPosCount;
    }

    /**
     * Auto generated setter method
     *
     * @param param PosCount
     */
    public void setPosCount(int param) {

      this.localPosCount = param;
    }

    /** field for Post */
    protected String localPost;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localPostTracker = false;

    public boolean isPostSpecified() {
      return localPostTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getPost() {
      return localPost;
    }

    /**
     * Auto generated setter method
     *
     * @param param Post
     */
    public void setPost(String param) {
      localPostTracker = param != null;

      this.localPost = param;
    }

    /** field for Region */
    protected String localRegion;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localRegionTracker = false;

    public boolean isRegionSpecified() {
      return localRegionTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getRegion() {
      return localRegion;
    }

    /**
     * Auto generated setter method
     *
     * @param param Region
     */
    public void setRegion(String param) {
      localRegionTracker = param != null;

      this.localRegion = param;
    }

    /** field for SecondLevelDeptFullName */
    protected String localSecondLevelDeptFullName;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localSecondLevelDeptFullNameTracker = false;

    public boolean isSecondLevelDeptFullNameSpecified() {
      return localSecondLevelDeptFullNameTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getSecondLevelDeptFullName() {
      return localSecondLevelDeptFullName;
    }

    /**
     * Auto generated setter method
     *
     * @param param SecondLevelDeptFullName
     */
    public void setSecondLevelDeptFullName(String param) {
      localSecondLevelDeptFullNameTracker = param != null;

      this.localSecondLevelDeptFullName = param;
    }

    /** field for SimpleNameCode */
    protected String localSimpleNameCode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localSimpleNameCodeTracker = false;

    public boolean isSimpleNameCodeSpecified() {
      return localSimpleNameCodeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getSimpleNameCode() {
      return localSimpleNameCode;
    }

    /**
     * Auto generated setter method
     *
     * @param param SimpleNameCode
     */
    public void setSimpleNameCode(String param) {
      localSimpleNameCodeTracker = param != null;

      this.localSimpleNameCode = param;
    }

    /** field for Status */
    protected String localStatus;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localStatusTracker = false;

    public boolean isStatusSpecified() {
      return localStatusTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getStatus() {
      return localStatus;
    }

    /**
     * Auto generated setter method
     *
     * @param param Status
     */
    public void setStatus(String param) {
      localStatusTracker = param != null;

      this.localStatus = param;
    }

    /** field for Street */
    protected String localStreet;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localStreetTracker = false;

    public boolean isStreetSpecified() {
      return localStreetTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getStreet() {
      return localStreet;
    }

    /**
     * Auto generated setter method
     *
     * @param param Street
     */
    public void setStreet(String param) {
      localStreetTracker = param != null;

      this.localStreet = param;
    }

    /** field for Telephone */
    protected String localTelephone;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localTelephoneTracker = false;

    public boolean isTelephoneSpecified() {
      return localTelephoneTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getTelephone() {
      return localTelephone;
    }

    /**
     * Auto generated setter method
     *
     * @param param Telephone
     */
    public void setTelephone(String param) {
      localTelephoneTracker = param != null;

      this.localTelephone = param;
    }

    /** field for UpdateBy */
    protected String localUpdateBy;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localUpdateByTracker = false;

    public boolean isUpdateBySpecified() {
      return localUpdateByTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getUpdateBy() {
      return localUpdateBy;
    }

    /**
     * Auto generated setter method
     *
     * @param param UpdateBy
     */
    public void setUpdateBy(String param) {
      localUpdateByTracker = param != null;

      this.localUpdateBy = param;
    }

    /** field for UpdateTime */
    protected java.util.Calendar localUpdateTime;

    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getUpdateTime() {
      return localUpdateTime;
    }

    /**
     * Auto generated setter method
     *
     * @param param UpdateTime
     */
    public void setUpdateTime(java.util.Calendar param) {

      this.localUpdateTime = param;
    }

    /** field for UserCount */
    protected int localUserCount;

    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getUserCount() {
      return localUserCount;
    }

    /**
     * Auto generated setter method
     *
     * @param param UserCount
     */
    public void setUserCount(int param) {

      this.localUserCount = param;
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, parentQName));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      String prefix = null;
      String namespace = null;

      prefix = parentQName.getPrefix();
      namespace = parentQName.getNamespaceURI();
      writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

      if (serializeType) {

        String namespacePrefix = registerPrefix(xmlWriter, "http://tempuri.org/");
        if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              namespacePrefix + ":ADDepartment",
              xmlWriter);
        } else {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              "ADDepartment",
              xmlWriter);
        }
      }
      if (localColumn1Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column1", xmlWriter);

        if (localColumn1 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column1 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn1);
        }

        xmlWriter.writeEndElement();
      }
      if (localColumn2Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column2", xmlWriter);

        if (localColumn2 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column2 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn2);
        }

        xmlWriter.writeEndElement();
      }
      if (localColumn3Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column3", xmlWriter);

        if (localColumn3 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column3 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn3);
        }

        xmlWriter.writeEndElement();
      }
      if (localColumn4Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column4", xmlWriter);

        if (localColumn4 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column4 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn4);
        }

        xmlWriter.writeEndElement();
      }
      if (localColumn5Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column5", xmlWriter);

        if (localColumn5 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column5 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn5);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "CreateTime", xmlWriter);

      if (localCreateTime == null) {
        // write the nil attribute

        throw new org.apache.axis2.databinding.ADBException("CreateTime cannot be null!!");

      } else {

        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCreateTime));
      }

      xmlWriter.writeEndElement();
      if (localCreatorTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Creator", xmlWriter);

        if (localCreator == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Creator cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localCreator);
        }

        xmlWriter.writeEndElement();
      }
      if (localDeptCodeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "DeptCode", xmlWriter);

        if (localDeptCode == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("DeptCode cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localDeptCode);
        }

        xmlWriter.writeEndElement();
      }
      if (localDeptdutyTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Deptduty", xmlWriter);

        if (localDeptduty == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Deptduty cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localDeptduty);
        }

        xmlWriter.writeEndElement();
      }
      if (localDeptFullNameTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "DeptFullName", xmlWriter);

        if (localDeptFullName == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("DeptFullName cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localDeptFullName);
        }

        xmlWriter.writeEndElement();
      }
      if (localLineTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Line", xmlWriter);

        if (localLine == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Line cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localLine);
        }

        xmlWriter.writeEndElement();
      }
      if (localDeptIDTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "DeptID", xmlWriter);

        if (localDeptID == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("DeptID cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localDeptID);
        }

        xmlWriter.writeEndElement();
      }
      if (localDeptNameTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "DeptName", xmlWriter);

        if (localDeptName == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("DeptName cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localDeptName);
        }

        xmlWriter.writeEndElement();
      }
      if (localFaxTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Fax", xmlWriter);

        if (localFax == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Fax cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localFax);
        }

        xmlWriter.writeEndElement();
      }
      if (localGroupMailBoxTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "GroupMailBox", xmlWriter);

        if (localGroupMailBox == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("GroupMailBox cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localGroupMailBox);
        }

        xmlWriter.writeEndElement();
      }
      if (localGroupNameTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "GroupName", xmlWriter);

        if (localGroupName == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("GroupName cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localGroupName);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "IsVOrg", xmlWriter);

      if (localIsVOrg == Integer.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("IsVOrg cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIsVOrg));
      }

      xmlWriter.writeEndElement();

      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "Level", xmlWriter);

      if (localLevel == Integer.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("Level cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLevel));
      }

      xmlWriter.writeEndElement();

      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "OrderNo", xmlWriter);

      if (localOrderNo == Integer.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("OrderNo cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOrderNo));
      }

      xmlWriter.writeEndElement();
      if (localParentDeptCodeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "ParentDeptCode", xmlWriter);

        if (localParentDeptCode == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("ParentDeptCode cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localParentDeptCode);
        }

        xmlWriter.writeEndElement();
      }
      if (localParentDeptIDTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "ParentDeptID", xmlWriter);

        if (localParentDeptID == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("ParentDeptID cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localParentDeptID);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "PosCount", xmlWriter);

      if (localPosCount == Integer.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("PosCount cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPosCount));
      }

      xmlWriter.writeEndElement();
      if (localPostTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Post", xmlWriter);

        if (localPost == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Post cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localPost);
        }

        xmlWriter.writeEndElement();
      }
      if (localRegionTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Region", xmlWriter);

        if (localRegion == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Region cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localRegion);
        }

        xmlWriter.writeEndElement();
      }
      if (localSecondLevelDeptFullNameTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "SecondLevelDeptFullName", xmlWriter);

        if (localSecondLevelDeptFullName == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException(
              "SecondLevelDeptFullName cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localSecondLevelDeptFullName);
        }

        xmlWriter.writeEndElement();
      }
      if (localSimpleNameCodeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "SimpleNameCode", xmlWriter);

        if (localSimpleNameCode == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("SimpleNameCode cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localSimpleNameCode);
        }

        xmlWriter.writeEndElement();
      }
      if (localStatusTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Status", xmlWriter);

        if (localStatus == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Status cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localStatus);
        }

        xmlWriter.writeEndElement();
      }
      if (localStreetTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Street", xmlWriter);

        if (localStreet == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Street cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localStreet);
        }

        xmlWriter.writeEndElement();
      }
      if (localTelephoneTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Telephone", xmlWriter);

        if (localTelephone == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Telephone cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localTelephone);
        }

        xmlWriter.writeEndElement();
      }
      if (localUpdateByTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "UpdateBy", xmlWriter);

        if (localUpdateBy == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("UpdateBy cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localUpdateBy);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "UpdateTime", xmlWriter);

      if (localUpdateTime == null) {
        // write the nil attribute

        throw new org.apache.axis2.databinding.ADBException("UpdateTime cannot be null!!");

      } else {

        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUpdateTime));
      }

      xmlWriter.writeEndElement();

      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "UserCount", xmlWriter);

      if (localUserCount == Integer.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("UserCount cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUserCount));
      }

      xmlWriter.writeEndElement();

      xmlWriter.writeEndElement();
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://tempuri.org/")) {
        return "ns1";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static ADDepartment parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        ADDepartment object = new ADDepartment();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type")
              != null) {
            String fullTypeName =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (fullTypeName != null) {
              String nsPrefix = null;
              if (fullTypeName.indexOf(":") > -1) {
                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
              }
              nsPrefix = nsPrefix == null ? "" : nsPrefix;

              String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

              if (!"ADDepartment".equals(type)) {
                // find namespace for the prefix
                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                return (ADDepartment) ExtensionMapper.getTypeObject(nsUri, type, reader);
              }
            }
          }

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          reader.next();

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column1")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column1" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn1(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column2")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column2" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn2(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column3")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column3" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn3(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column4")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column4" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn4(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column5")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column5" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn5(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "CreateTime")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "CreateTime" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setCreateTime(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Creator")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Creator" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setCreator(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "DeptCode")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "DeptCode" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setDeptCode(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Deptduty")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Deptduty" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setDeptduty(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "DeptFullName")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "DeptFullName" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setDeptFullName(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Line")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Line" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setLine(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "DeptID")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "DeptID" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setDeptID(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "DeptName")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "DeptName" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setDeptName(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Fax")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Fax" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setFax(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "GroupMailBox")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "GroupMailBox" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setGroupMailBox(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "GroupName")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "GroupName" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setGroupName(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "IsVOrg")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "IsVOrg" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setIsVOrg(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Level")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Level" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setLevel(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "OrderNo")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "OrderNo" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setOrderNo(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "ParentDeptCode")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "ParentDeptCode" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setParentDeptCode(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "ParentDeptID")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "ParentDeptID" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setParentDeptID(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "PosCount")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "PosCount" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPosCount(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Post")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Post" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPost(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Region")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Region" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setRegion(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "SecondLevelDeptFullName")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "SecondLevelDeptFullName" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setSecondLevelDeptFullName(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "SimpleNameCode")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "SimpleNameCode" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setSimpleNameCode(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Status")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Status" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setStatus(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Street")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Street" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setStreet(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Telephone")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Telephone" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setTelephone(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "UpdateBy")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "UpdateBy" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setUpdateBy(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "UpdateTime")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "UpdateTime" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setUpdateTime(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "UserCount")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "UserCount" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setUserCount(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement())
            // 2 - A start element we are not expecting indicates a trailing invalid property

            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  public static class ADUser implements org.apache.axis2.databinding.ADBBean {
    /* This type was generated from the piece of schema that had
    name = ADUser
    Namespace URI = http://tempuri.org/
    Namespace Prefix = ns1
    */

    /** field for AccountName */
    protected String localAccountName;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localAccountNameTracker = false;

    public boolean isAccountNameSpecified() {
      return localAccountNameTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getAccountName() {
      return localAccountName;
    }

    /**
     * Auto generated setter method
     *
     * @param param AccountName
     */
    public void setAccountName(String param) {
      localAccountNameTracker = param != null;

      this.localAccountName = param;
    }

    /** field for Birthday */
    protected java.util.Calendar localBirthday;

    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getBirthday() {
      return localBirthday;
    }

    /**
     * Auto generated setter method
     *
     * @param param Birthday
     */
    public void setBirthday(java.util.Calendar param) {

      this.localBirthday = param;
    }

    /** field for ChsName */
    protected String localChsName;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localChsNameTracker = false;

    public boolean isChsNameSpecified() {
      return localChsNameTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getChsName() {
      return localChsName;
    }

    /**
     * Auto generated setter method
     *
     * @param param ChsName
     */
    public void setChsName(String param) {
      localChsNameTracker = param != null;

      this.localChsName = param;
    }

    /** field for ChsSpell */
    protected String localChsSpell;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localChsSpellTracker = false;

    public boolean isChsSpellSpecified() {
      return localChsSpellTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getChsSpell() {
      return localChsSpell;
    }

    /**
     * Auto generated setter method
     *
     * @param param ChsSpell
     */
    public void setChsSpell(String param) {
      localChsSpellTracker = param != null;

      this.localChsSpell = param;
    }

    /** field for Column1 */
    protected String localColumn1;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn1Tracker = false;

    public boolean isColumn1Specified() {
      return localColumn1Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn1() {
      return localColumn1;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column1
     */
    public void setColumn1(String param) {
      localColumn1Tracker = param != null;

      this.localColumn1 = param;
    }

    /** field for Column2 */
    protected String localColumn2;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn2Tracker = false;

    public boolean isColumn2Specified() {
      return localColumn2Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn2() {
      return localColumn2;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column2
     */
    public void setColumn2(String param) {
      localColumn2Tracker = param != null;

      this.localColumn2 = param;
    }

    /** field for Column3 */
    protected String localColumn3;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn3Tracker = false;

    public boolean isColumn3Specified() {
      return localColumn3Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn3() {
      return localColumn3;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column3
     */
    public void setColumn3(String param) {
      localColumn3Tracker = param != null;

      this.localColumn3 = param;
    }

    /** field for Column4 */
    protected String localColumn4;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn4Tracker = false;

    public boolean isColumn4Specified() {
      return localColumn4Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn4() {
      return localColumn4;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column4
     */
    public void setColumn4(String param) {
      localColumn4Tracker = param != null;

      this.localColumn4 = param;
    }

    /** field for Column5 */
    protected String localColumn5;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localColumn5Tracker = false;

    public boolean isColumn5Specified() {
      return localColumn5Tracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getColumn5() {
      return localColumn5;
    }

    /**
     * Auto generated setter method
     *
     * @param param Column5
     */
    public void setColumn5(String param) {
      localColumn5Tracker = param != null;

      this.localColumn5 = param;
    }

    /** field for CreateTime */
    protected java.util.Calendar localCreateTime;

    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getCreateTime() {
      return localCreateTime;
    }

    /**
     * Auto generated setter method
     *
     * @param param CreateTime
     */
    public void setCreateTime(java.util.Calendar param) {

      this.localCreateTime = param;
    }

    /** field for Creator */
    protected String localCreator;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localCreatorTracker = false;

    public boolean isCreatorSpecified() {
      return localCreatorTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getCreator() {
      return localCreator;
    }

    /**
     * Auto generated setter method
     *
     * @param param Creator
     */
    public void setCreator(String param) {
      localCreatorTracker = param != null;

      this.localCreator = param;
    }

    /** field for Dept */
    protected String localDept;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDeptTracker = false;

    public boolean isDeptSpecified() {
      return localDeptTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getDept() {
      return localDept;
    }

    /**
     * Auto generated setter method
     *
     * @param param Dept
     */
    public void setDept(String param) {
      localDeptTracker = param != null;

      this.localDept = param;
    }

    /** field for DeptCode */
    protected String localDeptCode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDeptCodeTracker = false;

    public boolean isDeptCodeSpecified() {
      return localDeptCodeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getDeptCode() {
      return localDeptCode;
    }

    /**
     * Auto generated setter method
     *
     * @param param DeptCode
     */
    public void setDeptCode(String param) {
      localDeptCodeTracker = param != null;

      this.localDeptCode = param;
    }

    /** field for DeptID */
    protected String localDeptID;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDeptIDTracker = false;

    public boolean isDeptIDSpecified() {
      return localDeptIDTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getDeptID() {
      return localDeptID;
    }

    /**
     * Auto generated setter method
     *
     * @param param DeptID
     */
    public void setDeptID(String param) {
      localDeptIDTracker = param != null;

      this.localDeptID = param;
    }

    /** field for Diploma */
    protected String localDiploma;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDiplomaTracker = false;

    public boolean isDiplomaSpecified() {
      return localDiplomaTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getDiploma() {
      return localDiploma;
    }

    /**
     * Auto generated setter method
     *
     * @param param Diploma
     */
    public void setDiploma(String param) {
      localDiplomaTracker = param != null;

      this.localDiploma = param;
    }

    /** field for EmailAddress */
    protected String localEmailAddress;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localEmailAddressTracker = false;

    public boolean isEmailAddressSpecified() {
      return localEmailAddressTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getEmailAddress() {
      return localEmailAddress;
    }

    /**
     * Auto generated setter method
     *
     * @param param EmailAddress
     */
    public void setEmailAddress(String param) {
      localEmailAddressTracker = param != null;

      this.localEmailAddress = param;
    }

    /** field for Fax */
    protected String localFax;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localFaxTracker = false;

    public boolean isFaxSpecified() {
      return localFaxTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getFax() {
      return localFax;
    }

    /**
     * Auto generated setter method
     *
     * @param param Fax
     */
    public void setFax(String param) {
      localFaxTracker = param != null;

      this.localFax = param;
    }

    /** field for FirstName */
    protected String localFirstName;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localFirstNameTracker = false;

    public boolean isFirstNameSpecified() {
      return localFirstNameTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getFirstName() {
      return localFirstName;
    }

    /**
     * Auto generated setter method
     *
     * @param param FirstName
     */
    public void setFirstName(String param) {
      localFirstNameTracker = param != null;

      this.localFirstName = param;
    }

    /** field for HomePhone */
    protected String localHomePhone;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localHomePhoneTracker = false;

    public boolean isHomePhoneSpecified() {
      return localHomePhoneTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getHomePhone() {
      return localHomePhone;
    }

    /**
     * Auto generated setter method
     *
     * @param param HomePhone
     */
    public void setHomePhone(String param) {
      localHomePhoneTracker = param != null;

      this.localHomePhone = param;
    }

    /** field for Hometown */
    protected String localHometown;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localHometownTracker = false;

    public boolean isHometownSpecified() {
      return localHometownTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getHometown() {
      return localHometown;
    }

    /**
     * Auto generated setter method
     *
     * @param param Hometown
     */
    public void setHometown(String param) {
      localHometownTracker = param != null;

      this.localHometown = param;
    }

    /** field for IdentityID */
    protected String localIdentityID;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localIdentityIDTracker = false;

    public boolean isIdentityIDSpecified() {
      return localIdentityIDTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getIdentityID() {
      return localIdentityID;
    }

    /**
     * Auto generated setter method
     *
     * @param param IdentityID
     */
    public void setIdentityID(String param) {
      localIdentityIDTracker = param != null;

      this.localIdentityID = param;
    }

    /** field for IsMainAccount */
    protected int localIsMainAccount;

    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getIsMainAccount() {
      return localIsMainAccount;
    }

    /**
     * Auto generated setter method
     *
     * @param param IsMainAccount
     */
    public void setIsMainAccount(int param) {

      this.localIsMainAccount = param;
    }

    /** field for IsVUser */
    protected int localIsVUser;

    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getIsVUser() {
      return localIsVUser;
    }

    /**
     * Auto generated setter method
     *
     * @param param IsVUser
     */
    public void setIsVUser(int param) {

      this.localIsVUser = param;
    }

    /** field for Job */
    protected String localJob;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localJobTracker = false;

    public boolean isJobSpecified() {
      return localJobTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getJob() {
      return localJob;
    }

    /**
     * Auto generated setter method
     *
     * @param param Job
     */
    public void setJob(String param) {
      localJobTracker = param != null;

      this.localJob = param;
    }

    /** field for JobAppointDate */
    protected java.util.Calendar localJobAppointDate;

    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getJobAppointDate() {
      return localJobAppointDate;
    }

    /**
     * Auto generated setter method
     *
     * @param param JobAppointDate
     */
    public void setJobAppointDate(java.util.Calendar param) {

      this.localJobAppointDate = param;
    }

    /** field for JobAsseDate */
    protected java.util.Calendar localJobAsseDate;

    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getJobAsseDate() {
      return localJobAsseDate;
    }

    /**
     * Auto generated setter method
     *
     * @param param JobAsseDate
     */
    public void setJobAsseDate(java.util.Calendar param) {

      this.localJobAsseDate = param;
    }

    /** field for JobCode */
    protected String localJobCode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localJobCodeTracker = false;

    public boolean isJobCodeSpecified() {
      return localJobCodeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getJobCode() {
      return localJobCode;
    }

    /**
     * Auto generated setter method
     *
     * @param param JobCode
     */
    public void setJobCode(String param) {
      localJobCodeTracker = param != null;

      this.localJobCode = param;
    }

    /** field for JobLevel */
    protected String localJobLevel;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localJobLevelTracker = false;

    public boolean isJobLevelSpecified() {
      return localJobLevelTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getJobLevel() {
      return localJobLevel;
    }

    /**
     * Auto generated setter method
     *
     * @param param JobLevel
     */
    public void setJobLevel(String param) {
      localJobLevelTracker = param != null;

      this.localJobLevel = param;
    }

    /** field for JobType */
    protected String localJobType;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localJobTypeTracker = false;

    public boolean isJobTypeSpecified() {
      return localJobTypeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getJobType() {
      return localJobType;
    }

    /**
     * Auto generated setter method
     *
     * @param param JobType
     */
    public void setJobType(String param) {
      localJobTypeTracker = param != null;

      this.localJobType = param;
    }

    /** field for JoinDate */
    protected String localJoinDate;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localJoinDateTracker = false;

    public boolean isJoinDateSpecified() {
      return localJoinDateTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getJoinDate() {
      return localJoinDate;
    }

    /**
     * Auto generated setter method
     *
     * @param param JoinDate
     */
    public void setJoinDate(String param) {
      localJoinDateTracker = param != null;

      this.localJoinDate = param;
    }

    /** field for LastName */
    protected String localLastName;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localLastNameTracker = false;

    public boolean isLastNameSpecified() {
      return localLastNameTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getLastName() {
      return localLastName;
    }

    /**
     * Auto generated setter method
     *
     * @param param LastName
     */
    public void setLastName(String param) {
      localLastNameTracker = param != null;

      this.localLastName = param;
    }

    /** field for LastUpdateDate */
    protected java.util.Calendar localLastUpdateDate;

    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getLastUpdateDate() {
      return localLastUpdateDate;
    }

    /**
     * Auto generated setter method
     *
     * @param param LastUpdateDate
     */
    public void setLastUpdateDate(java.util.Calendar param) {

      this.localLastUpdateDate = param;
    }

    /** field for LevelType */
    protected int localLevelType;

    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getLevelType() {
      return localLevelType;
    }

    /**
     * Auto generated setter method
     *
     * @param param LevelType
     */
    public void setLevelType(int param) {

      this.localLevelType = param;
    }

    /** field for MaritalStatus */
    protected String localMaritalStatus;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localMaritalStatusTracker = false;

    public boolean isMaritalStatusSpecified() {
      return localMaritalStatusTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getMaritalStatus() {
      return localMaritalStatus;
    }

    /**
     * Auto generated setter method
     *
     * @param param MaritalStatus
     */
    public void setMaritalStatus(String param) {
      localMaritalStatusTracker = param != null;

      this.localMaritalStatus = param;
    }

    /** field for MobilePhone */
    protected String localMobilePhone;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localMobilePhoneTracker = false;

    public boolean isMobilePhoneSpecified() {
      return localMobilePhoneTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getMobilePhone() {
      return localMobilePhone;
    }

    /**
     * Auto generated setter method
     *
     * @param param MobilePhone
     */
    public void setMobilePhone(String param) {
      localMobilePhoneTracker = param != null;

      this.localMobilePhone = param;
    }

    /** field for Nation */
    protected String localNation;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localNationTracker = false;

    public boolean isNationSpecified() {
      return localNationTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getNation() {
      return localNation;
    }

    /**
     * Auto generated setter method
     *
     * @param param Nation
     */
    public void setNation(String param) {
      localNationTracker = param != null;

      this.localNation = param;
    }

    /** field for OfficePhone */
    protected String localOfficePhone;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localOfficePhoneTracker = false;

    public boolean isOfficePhoneSpecified() {
      return localOfficePhoneTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getOfficePhone() {
      return localOfficePhone;
    }

    /**
     * Auto generated setter method
     *
     * @param param OfficePhone
     */
    public void setOfficePhone(String param) {
      localOfficePhoneTracker = param != null;

      this.localOfficePhone = param;
    }

    /** field for PoisitionClassCode */
    protected String localPoisitionClassCode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localPoisitionClassCodeTracker = false;

    public boolean isPoisitionClassCodeSpecified() {
      return localPoisitionClassCodeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getPoisitionClassCode() {
      return localPoisitionClassCode;
    }

    /**
     * Auto generated setter method
     *
     * @param param PoisitionClassCode
     */
    public void setPoisitionClassCode(String param) {
      localPoisitionClassCodeTracker = param != null;

      this.localPoisitionClassCode = param;
    }

    /** field for Politics */
    protected String localPolitics;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localPoliticsTracker = false;

    public boolean isPoliticsSpecified() {
      return localPoliticsTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getPolitics() {
      return localPolitics;
    }

    /**
     * Auto generated setter method
     *
     * @param param Politics
     */
    public void setPolitics(String param) {
      localPoliticsTracker = param != null;

      this.localPolitics = param;
    }

    /** field for Position */
    protected String localPosition;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localPositionTracker = false;

    public boolean isPositionSpecified() {
      return localPositionTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getPosition() {
      return localPosition;
    }

    /**
     * Auto generated setter method
     *
     * @param param Position
     */
    public void setPosition(String param) {
      localPositionTracker = param != null;

      this.localPosition = param;
    }

    /** field for PositionAppDate */
    protected String localPositionAppDate;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localPositionAppDateTracker = false;

    public boolean isPositionAppDateSpecified() {
      return localPositionAppDateTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getPositionAppDate() {
      return localPositionAppDate;
    }

    /**
     * Auto generated setter method
     *
     * @param param PositionAppDate
     */
    public void setPositionAppDate(String param) {
      localPositionAppDateTracker = param != null;

      this.localPositionAppDate = param;
    }

    /** field for PositionCode */
    protected String localPositionCode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localPositionCodeTracker = false;

    public boolean isPositionCodeSpecified() {
      return localPositionCodeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getPositionCode() {
      return localPositionCode;
    }

    /**
     * Auto generated setter method
     *
     * @param param PositionCode
     */
    public void setPositionCode(String param) {
      localPositionCodeTracker = param != null;

      this.localPositionCode = param;
    }

    /** field for PositionSeries */
    protected String localPositionSeries;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localPositionSeriesTracker = false;

    public boolean isPositionSeriesSpecified() {
      return localPositionSeriesTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getPositionSeries() {
      return localPositionSeries;
    }

    /**
     * Auto generated setter method
     *
     * @param param PositionSeries
     */
    public void setPositionSeries(String param) {
      localPositionSeriesTracker = param != null;

      this.localPositionSeries = param;
    }

    /** field for PositionType */
    protected String localPositionType;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localPositionTypeTracker = false;

    public boolean isPositionTypeSpecified() {
      return localPositionTypeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getPositionType() {
      return localPositionType;
    }

    /**
     * Auto generated setter method
     *
     * @param param PositionType
     */
    public void setPositionType(String param) {
      localPositionTypeTracker = param != null;

      this.localPositionType = param;
    }

    /** field for School */
    protected String localSchool;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localSchoolTracker = false;

    public boolean isSchoolSpecified() {
      return localSchoolTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getSchool() {
      return localSchool;
    }

    /**
     * Auto generated setter method
     *
     * @param param School
     */
    public void setSchool(String param) {
      localSchoolTracker = param != null;

      this.localSchool = param;
    }

    /** field for Sex */
    protected String localSex;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localSexTracker = false;

    public boolean isSexSpecified() {
      return localSexTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getSex() {
      return localSex;
    }

    /**
     * Auto generated setter method
     *
     * @param param Sex
     */
    public void setSex(String param) {
      localSexTracker = param != null;

      this.localSex = param;
    }

    /** field for Status */
    protected String localStatus;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localStatusTracker = false;

    public boolean isStatusSpecified() {
      return localStatusTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getStatus() {
      return localStatus;
    }

    /**
     * Auto generated setter method
     *
     * @param param Status
     */
    public void setStatus(String param) {
      localStatusTracker = param != null;

      this.localStatus = param;
    }

    /** field for UpdateBy */
    protected String localUpdateBy;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localUpdateByTracker = false;

    public boolean isUpdateBySpecified() {
      return localUpdateByTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getUpdateBy() {
      return localUpdateBy;
    }

    /**
     * Auto generated setter method
     *
     * @param param UpdateBy
     */
    public void setUpdateBy(String param) {
      localUpdateByTracker = param != null;

      this.localUpdateBy = param;
    }

    /** field for UpdateTime */
    protected java.util.Calendar localUpdateTime;

    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getUpdateTime() {
      return localUpdateTime;
    }

    /**
     * Auto generated setter method
     *
     * @param param UpdateTime
     */
    public void setUpdateTime(java.util.Calendar param) {

      this.localUpdateTime = param;
    }

    /** field for UserCode */
    protected String localUserCode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localUserCodeTracker = false;

    public boolean isUserCodeSpecified() {
      return localUserCodeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getUserCode() {
      return localUserCode;
    }

    /**
     * Auto generated setter method
     *
     * @param param UserCode
     */
    public void setUserCode(String param) {
      localUserCodeTracker = param != null;

      this.localUserCode = param;
    }

    /** field for UserID */
    protected String localUserID;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localUserIDTracker = false;

    public boolean isUserIDSpecified() {
      return localUserIDTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getUserID() {
      return localUserID;
    }

    /**
     * Auto generated setter method
     *
     * @param param UserID
     */
    public void setUserID(String param) {
      localUserIDTracker = param != null;

      this.localUserID = param;
    }

    /** field for Vocational */
    protected String localVocational;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localVocationalTracker = false;

    public boolean isVocationalSpecified() {
      return localVocationalTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getVocational() {
      return localVocational;
    }

    /**
     * Auto generated setter method
     *
     * @param param Vocational
     */
    public void setVocational(String param) {
      localVocationalTracker = param != null;

      this.localVocational = param;
    }

    /** field for WorkDate */
    protected java.util.Calendar localWorkDate;

    /**
     * Auto generated getter method
     *
     * @return java.util.Calendar
     */
    public java.util.Calendar getWorkDate() {
      return localWorkDate;
    }

    /**
     * Auto generated setter method
     *
     * @param param WorkDate
     */
    public void setWorkDate(java.util.Calendar param) {

      this.localWorkDate = param;
    }

    /** field for Line */
    protected String localLine;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localLineTracker = false;

    public boolean isLineSpecified() {
      return localLineTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getLine() {
      return localLine;
    }

    /**
     * Auto generated setter method
     *
     * @param param Line
     */
    public void setLine(String param) {
      localLineTracker = param != null;

      this.localLine = param;
    }

    /** field for OutDate */
    protected String localOutDate;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localOutDateTracker = false;

    public boolean isOutDateSpecified() {
      return localOutDateTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getOutDate() {
      return localOutDate;
    }

    /**
     * Auto generated setter method
     *
     * @param param OutDate
     */
    public void setOutDate(String param) {
      localOutDateTracker = param != null;

      this.localOutDate = param;
    }

    /** field for BeforeWorkYear */
    protected String localBeforeWorkYear;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localBeforeWorkYearTracker = false;

    public boolean isBeforeWorkYearSpecified() {
      return localBeforeWorkYearTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getBeforeWorkYear() {
      return localBeforeWorkYear;
    }

    /**
     * Auto generated setter method
     *
     * @param param BeforeWorkYear
     */
    public void setBeforeWorkYear(String param) {
      localBeforeWorkYearTracker = param != null;

      this.localBeforeWorkYear = param;
    }

    /** field for CurrentWorkYear */
    protected String localCurrentWorkYear;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localCurrentWorkYearTracker = false;

    public boolean isCurrentWorkYearSpecified() {
      return localCurrentWorkYearTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getCurrentWorkYear() {
      return localCurrentWorkYear;
    }

    /**
     * Auto generated setter method
     *
     * @param param CurrentWorkYear
     */
    public void setCurrentWorkYear(String param) {
      localCurrentWorkYearTracker = param != null;

      this.localCurrentWorkYear = param;
    }

    /** field for IsAD */
    protected int localIsAD;

    /**
     * Auto generated getter method
     *
     * @return int
     */
    public int getIsAD() {
      return localIsAD;
    }

    /**
     * Auto generated setter method
     *
     * @param param IsAD
     */
    public void setIsAD(int param) {

      this.localIsAD = param;
    }

    /** field for RegisterNumber */
    protected String localRegisterNumber;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localRegisterNumberTracker = false;

    public boolean isRegisterNumberSpecified() {
      return localRegisterNumberTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getRegisterNumber() {
      return localRegisterNumber;
    }

    /**
     * Auto generated setter method
     *
     * @param param RegisterNumber
     */
    public void setRegisterNumber(String param) {
      localRegisterNumberTracker = param != null;

      this.localRegisterNumber = param;
    }

    /** field for WorkHourType */
    protected String localWorkHourType;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localWorkHourTypeTracker = false;

    public boolean isWorkHourTypeSpecified() {
      return localWorkHourTypeTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getWorkHourType() {
      return localWorkHourType;
    }

    /**
     * Auto generated setter method
     *
     * @param param WorkHourType
     */
    public void setWorkHourType(String param) {
      localWorkHourTypeTracker = param != null;

      this.localWorkHourType = param;
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, parentQName));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      String prefix = null;
      String namespace = null;

      prefix = parentQName.getPrefix();
      namespace = parentQName.getNamespaceURI();
      writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

      if (serializeType) {

        String namespacePrefix = registerPrefix(xmlWriter, "http://tempuri.org/");
        if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              namespacePrefix + ":ADUser",
              xmlWriter);
        } else {
          writeAttribute(
              "xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ADUser", xmlWriter);
        }
      }
      if (localAccountNameTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "AccountName", xmlWriter);

        if (localAccountName == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("AccountName cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localAccountName);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "Birthday", xmlWriter);

      if (localBirthday == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {

        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localBirthday));
      }

      xmlWriter.writeEndElement();
      if (localChsNameTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "ChsName", xmlWriter);

        if (localChsName == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("ChsName cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localChsName);
        }

        xmlWriter.writeEndElement();
      }
      if (localChsSpellTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "ChsSpell", xmlWriter);

        if (localChsSpell == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("ChsSpell cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localChsSpell);
        }

        xmlWriter.writeEndElement();
      }
      if (localColumn1Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column1", xmlWriter);

        if (localColumn1 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column1 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn1);
        }

        xmlWriter.writeEndElement();
      }
      if (localColumn2Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column2", xmlWriter);

        if (localColumn2 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column2 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn2);
        }

        xmlWriter.writeEndElement();
      }
      if (localColumn3Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column3", xmlWriter);

        if (localColumn3 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column3 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn3);
        }

        xmlWriter.writeEndElement();
      }
      if (localColumn4Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column4", xmlWriter);

        if (localColumn4 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column4 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn4);
        }

        xmlWriter.writeEndElement();
      }
      if (localColumn5Tracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Column5", xmlWriter);

        if (localColumn5 == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Column5 cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localColumn5);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "CreateTime", xmlWriter);

      if (localCreateTime == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {

        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCreateTime));
      }

      xmlWriter.writeEndElement();
      if (localCreatorTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Creator", xmlWriter);

        if (localCreator == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Creator cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localCreator);
        }

        xmlWriter.writeEndElement();
      }
      if (localDeptTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Dept", xmlWriter);

        if (localDept == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Dept cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localDept);
        }

        xmlWriter.writeEndElement();
      }
      if (localDeptCodeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "DeptCode", xmlWriter);

        if (localDeptCode == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("DeptCode cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localDeptCode);
        }

        xmlWriter.writeEndElement();
      }
      if (localDeptIDTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "DeptID", xmlWriter);

        if (localDeptID == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("DeptID cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localDeptID);
        }

        xmlWriter.writeEndElement();
      }
      if (localDiplomaTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Diploma", xmlWriter);

        if (localDiploma == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Diploma cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localDiploma);
        }

        xmlWriter.writeEndElement();
      }
      if (localEmailAddressTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "EmailAddress", xmlWriter);

        if (localEmailAddress == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("EmailAddress cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localEmailAddress);
        }

        xmlWriter.writeEndElement();
      }
      if (localFaxTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Fax", xmlWriter);

        if (localFax == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Fax cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localFax);
        }

        xmlWriter.writeEndElement();
      }
      if (localFirstNameTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "FirstName", xmlWriter);

        if (localFirstName == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("FirstName cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localFirstName);
        }

        xmlWriter.writeEndElement();
      }
      if (localHomePhoneTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "HomePhone", xmlWriter);

        if (localHomePhone == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("HomePhone cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localHomePhone);
        }

        xmlWriter.writeEndElement();
      }
      if (localHometownTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Hometown", xmlWriter);

        if (localHometown == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Hometown cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localHometown);
        }

        xmlWriter.writeEndElement();
      }
      if (localIdentityIDTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "IdentityID", xmlWriter);

        if (localIdentityID == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("IdentityID cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localIdentityID);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "IsMainAccount", xmlWriter);

      if (localIsMainAccount == Integer.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("IsMainAccount cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIsMainAccount));
      }

      xmlWriter.writeEndElement();

      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "IsVUser", xmlWriter);

      if (localIsVUser == Integer.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("IsVUser cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIsVUser));
      }

      xmlWriter.writeEndElement();
      if (localJobTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Job", xmlWriter);

        if (localJob == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Job cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localJob);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "JobAppointDate", xmlWriter);

      if (localJobAppointDate == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {

        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localJobAppointDate));
      }

      xmlWriter.writeEndElement();

      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "JobAsseDate", xmlWriter);

      if (localJobAsseDate == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {

        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localJobAsseDate));
      }

      xmlWriter.writeEndElement();
      if (localJobCodeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "JobCode", xmlWriter);

        if (localJobCode == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("JobCode cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localJobCode);
        }

        xmlWriter.writeEndElement();
      }
      if (localJobLevelTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "JobLevel", xmlWriter);

        if (localJobLevel == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("JobLevel cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localJobLevel);
        }

        xmlWriter.writeEndElement();
      }
      if (localJobTypeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "JobType", xmlWriter);

        if (localJobType == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("JobType cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localJobType);
        }

        xmlWriter.writeEndElement();
      }
      if (localJoinDateTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "JoinDate", xmlWriter);

        if (localJoinDate == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("JoinDate cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localJoinDate);
        }

        xmlWriter.writeEndElement();
      }
      if (localLastNameTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "LastName", xmlWriter);

        if (localLastName == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("LastName cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localLastName);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "LastUpdateDate", xmlWriter);

      if (localLastUpdateDate == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {

        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLastUpdateDate));
      }

      xmlWriter.writeEndElement();

      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "LevelType", xmlWriter);

      if (localLevelType == Integer.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("LevelType cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLevelType));
      }

      xmlWriter.writeEndElement();
      if (localMaritalStatusTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "MaritalStatus", xmlWriter);

        if (localMaritalStatus == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("MaritalStatus cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localMaritalStatus);
        }

        xmlWriter.writeEndElement();
      }
      if (localMobilePhoneTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "MobilePhone", xmlWriter);

        if (localMobilePhone == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("MobilePhone cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localMobilePhone);
        }

        xmlWriter.writeEndElement();
      }
      if (localNationTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Nation", xmlWriter);

        if (localNation == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Nation cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localNation);
        }

        xmlWriter.writeEndElement();
      }
      if (localOfficePhoneTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "OfficePhone", xmlWriter);

        if (localOfficePhone == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("OfficePhone cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localOfficePhone);
        }

        xmlWriter.writeEndElement();
      }
      if (localPoisitionClassCodeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "PoisitionClassCode", xmlWriter);

        if (localPoisitionClassCode == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException(
              "PoisitionClassCode cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localPoisitionClassCode);
        }

        xmlWriter.writeEndElement();
      }
      if (localPoliticsTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Politics", xmlWriter);

        if (localPolitics == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Politics cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localPolitics);
        }

        xmlWriter.writeEndElement();
      }
      if (localPositionTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Position", xmlWriter);

        if (localPosition == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Position cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localPosition);
        }

        xmlWriter.writeEndElement();
      }
      if (localPositionAppDateTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "PositionAppDate", xmlWriter);

        if (localPositionAppDate == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("PositionAppDate cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localPositionAppDate);
        }

        xmlWriter.writeEndElement();
      }
      if (localPositionCodeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "PositionCode", xmlWriter);

        if (localPositionCode == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("PositionCode cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localPositionCode);
        }

        xmlWriter.writeEndElement();
      }
      if (localPositionSeriesTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "PositionSeries", xmlWriter);

        if (localPositionSeries == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("PositionSeries cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localPositionSeries);
        }

        xmlWriter.writeEndElement();
      }
      if (localPositionTypeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "PositionType", xmlWriter);

        if (localPositionType == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("PositionType cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localPositionType);
        }

        xmlWriter.writeEndElement();
      }
      if (localSchoolTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "School", xmlWriter);

        if (localSchool == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("School cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localSchool);
        }

        xmlWriter.writeEndElement();
      }
      if (localSexTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Sex", xmlWriter);

        if (localSex == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Sex cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localSex);
        }

        xmlWriter.writeEndElement();
      }
      if (localStatusTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Status", xmlWriter);

        if (localStatus == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Status cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localStatus);
        }

        xmlWriter.writeEndElement();
      }
      if (localUpdateByTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "UpdateBy", xmlWriter);

        if (localUpdateBy == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("UpdateBy cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localUpdateBy);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "UpdateTime", xmlWriter);

      if (localUpdateTime == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {

        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUpdateTime));
      }

      xmlWriter.writeEndElement();
      if (localUserCodeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "UserCode", xmlWriter);

        if (localUserCode == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("UserCode cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localUserCode);
        }

        xmlWriter.writeEndElement();
      }
      if (localUserIDTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "UserID", xmlWriter);

        if (localUserID == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("UserID cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localUserID);
        }

        xmlWriter.writeEndElement();
      }
      if (localVocationalTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Vocational", xmlWriter);

        if (localVocational == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Vocational cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localVocational);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "WorkDate", xmlWriter);

      if (localWorkDate == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {

        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWorkDate));
      }

      xmlWriter.writeEndElement();
      if (localLineTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "Line", xmlWriter);

        if (localLine == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("Line cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localLine);
        }

        xmlWriter.writeEndElement();
      }
      if (localOutDateTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "OutDate", xmlWriter);

        if (localOutDate == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("OutDate cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localOutDate);
        }

        xmlWriter.writeEndElement();
      }
      if (localBeforeWorkYearTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "BeforeWorkYear", xmlWriter);

        if (localBeforeWorkYear == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("BeforeWorkYear cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localBeforeWorkYear);
        }

        xmlWriter.writeEndElement();
      }
      if (localCurrentWorkYearTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "CurrentWorkYear", xmlWriter);

        if (localCurrentWorkYear == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("CurrentWorkYear cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localCurrentWorkYear);
        }

        xmlWriter.writeEndElement();
      }
      namespace = "http://tempuri.org/";
      writeStartElement(null, namespace, "IsAD", xmlWriter);

      if (localIsAD == Integer.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("IsAD cannot be null!!");

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIsAD));
      }

      xmlWriter.writeEndElement();
      if (localRegisterNumberTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "RegisterNumber", xmlWriter);

        if (localRegisterNumber == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("RegisterNumber cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localRegisterNumber);
        }

        xmlWriter.writeEndElement();
      }
      if (localWorkHourTypeTracker) {
        namespace = "http://tempuri.org/";
        writeStartElement(null, namespace, "WorkHourType", xmlWriter);

        if (localWorkHourType == null) {
          // write the nil attribute

          throw new org.apache.axis2.databinding.ADBException("WorkHourType cannot be null!!");

        } else {

          xmlWriter.writeCharacters(localWorkHourType);
        }

        xmlWriter.writeEndElement();
      }
      xmlWriter.writeEndElement();
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://tempuri.org/")) {
        return "ns1";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static ADUser parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        ADUser object = new ADUser();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type")
              != null) {
            String fullTypeName =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (fullTypeName != null) {
              String nsPrefix = null;
              if (fullTypeName.indexOf(":") > -1) {
                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
              }
              nsPrefix = nsPrefix == null ? "" : nsPrefix;

              String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

              if (!"ADUser".equals(type)) {
                // find namespace for the prefix
                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                return (ADUser) ExtensionMapper.getTypeObject(nsUri, type, reader);
              }
            }
          }

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          reader.next();

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "AccountName")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "AccountName" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setAccountName(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Birthday")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

              String content = reader.getElementText();

              object.setBirthday(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

            } else {

              reader.getElementText(); // throw away text nodes if any.
            }

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "ChsName")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "ChsName" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setChsName(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "ChsSpell")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "ChsSpell" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setChsSpell(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column1")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column1" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn1(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column2")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column2" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn2(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column3")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column3" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn3(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column4")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column4" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn4(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Column5")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Column5" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setColumn5(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "CreateTime")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

              String content = reader.getElementText();

              object.setCreateTime(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

            } else {

              reader.getElementText(); // throw away text nodes if any.
            }

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Creator")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Creator" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setCreator(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Dept")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Dept" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setDept(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "DeptCode")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "DeptCode" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setDeptCode(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "DeptID")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "DeptID" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setDeptID(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Diploma")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Diploma" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setDiploma(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "EmailAddress")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "EmailAddress" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setEmailAddress(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Fax")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Fax" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setFax(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "FirstName")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "FirstName" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setFirstName(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "HomePhone")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "HomePhone" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setHomePhone(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Hometown")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Hometown" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setHometown(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "IdentityID")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "IdentityID" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setIdentityID(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "IsMainAccount")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "IsMainAccount" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setIsMainAccount(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "IsVUser")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "IsVUser" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setIsVUser(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Job")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Job" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setJob(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "JobAppointDate")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

              String content = reader.getElementText();

              object.setJobAppointDate(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

            } else {

              reader.getElementText(); // throw away text nodes if any.
            }

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "JobAsseDate")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

              String content = reader.getElementText();

              object.setJobAsseDate(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

            } else {

              reader.getElementText(); // throw away text nodes if any.
            }

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "JobCode")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "JobCode" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setJobCode(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "JobLevel")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "JobLevel" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setJobLevel(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "JobType")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "JobType" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setJobType(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "JoinDate")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "JoinDate" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setJoinDate(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "LastName")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "LastName" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setLastName(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "LastUpdateDate")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

              String content = reader.getElementText();

              object.setLastUpdateDate(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

            } else {

              reader.getElementText(); // throw away text nodes if any.
            }

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "LevelType")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "LevelType" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setLevelType(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "MaritalStatus")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "MaritalStatus" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setMaritalStatus(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "MobilePhone")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "MobilePhone" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setMobilePhone(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Nation")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Nation" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setNation(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "OfficePhone")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "OfficePhone" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setOfficePhone(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "PoisitionClassCode")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "PoisitionClassCode" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPoisitionClassCode(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Politics")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Politics" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPolitics(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Position")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Position" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPosition(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "PositionAppDate")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "PositionAppDate" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPositionAppDate(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "PositionCode")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "PositionCode" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPositionCode(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "PositionSeries")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "PositionSeries" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPositionSeries(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "PositionType")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "PositionType" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setPositionType(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "School")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "School" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setSchool(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Sex")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Sex" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setSex(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Status")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Status" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setStatus(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "UpdateBy")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "UpdateBy" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setUpdateBy(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "UpdateTime")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

              String content = reader.getElementText();

              object.setUpdateTime(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

            } else {

              reader.getElementText(); // throw away text nodes if any.
            }

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "UserCode")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "UserCode" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setUserCode(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "UserID")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "UserID" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setUserID(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Vocational")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Vocational" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setVocational(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "WorkDate")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

              String content = reader.getElementText();

              object.setWorkDate(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));

            } else {

              reader.getElementText(); // throw away text nodes if any.
            }

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "Line")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "Line" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setLine(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "OutDate")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "OutDate" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setOutDate(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "BeforeWorkYear")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "BeforeWorkYear" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setBeforeWorkYear(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "CurrentWorkYear")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "CurrentWorkYear" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setCurrentWorkYear(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "IsAD")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "IsAD" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setIsAD(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

            reader.next();

          } // End of if for expected property start element
          else {
            // 1 - A start element we are not expecting indicates an invalid parameter was passed
            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());
          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "RegisterNumber")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "RegisterNumber" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setRegisterNumber(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "WorkHourType")
                  .equals(reader.getName())) {

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              throw new org.apache.axis2.databinding.ADBException(
                  "The element: " + "WorkHourType" + "  cannot be null");
            }

            String content = reader.getElementText();

            object.setWorkHourType(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement())
            // 2 - A start element we are not expecting indicates a trailing invalid property

            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  public static class ArrayOfADDepartment implements org.apache.axis2.databinding.ADBBean {
    /* This type was generated from the piece of schema that had
    name = ArrayOfADDepartment
    Namespace URI = http://tempuri.org/
    Namespace Prefix = ns1
    */

    /** field for ADDepartment This was an Array! */
    protected ADDepartment[] localADDepartment;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localADDepartmentTracker = false;

    public boolean isADDepartmentSpecified() {
      return localADDepartmentTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return ADDepartment[]
     */
    public ADDepartment[] getADDepartment() {
      return localADDepartment;
    }

    /** validate the array for ADDepartment */
    protected void validateADDepartment(ADDepartment[] param) {}

    /**
     * Auto generated setter method
     *
     * @param param ADDepartment
     */
    public void setADDepartment(ADDepartment[] param) {

      validateADDepartment(param);

      localADDepartmentTracker = true;

      this.localADDepartment = param;
    }

    /**
     * Auto generated add method for the array for convenience
     *
     * @param param ADDepartment
     */
    public void addADDepartment(ADDepartment param) {
      if (localADDepartment == null) {
        localADDepartment = new ADDepartment[] {};
      }

      // update the setting tracker
      localADDepartmentTracker = true;

      java.util.List list =
          org.apache.axis2.databinding.utils.ConverterUtil.toList(localADDepartment);
      list.add(param);
      this.localADDepartment = (ADDepartment[]) list.toArray(new ADDepartment[list.size()]);
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, parentQName));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      String prefix = null;
      String namespace = null;

      prefix = parentQName.getPrefix();
      namespace = parentQName.getNamespaceURI();
      writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

      if (serializeType) {

        String namespacePrefix = registerPrefix(xmlWriter, "http://tempuri.org/");
        if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              namespacePrefix + ":ArrayOfADDepartment",
              xmlWriter);
        } else {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              "ArrayOfADDepartment",
              xmlWriter);
        }
      }
      if (localADDepartmentTracker) {
        if (localADDepartment != null) {
          for (int i = 0; i < localADDepartment.length; i++) {
            if (localADDepartment[i] != null) {
              localADDepartment[i].serialize(
                  new javax.xml.namespace.QName("http://tempuri.org/", "ADDepartment"), xmlWriter);
            } else {

              writeStartElement(null, "http://tempuri.org/", "ADDepartment", xmlWriter);

              // write the nil attribute
              writeAttribute(
                  "xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
              xmlWriter.writeEndElement();
            }
          }
        } else {

          writeStartElement(null, "http://tempuri.org/", "ADDepartment", xmlWriter);

          // write the nil attribute
          writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
          xmlWriter.writeEndElement();
        }
      }
      xmlWriter.writeEndElement();
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://tempuri.org/")) {
        return "ns1";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static ArrayOfADDepartment parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        ArrayOfADDepartment object = new ArrayOfADDepartment();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type")
              != null) {
            String fullTypeName =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (fullTypeName != null) {
              String nsPrefix = null;
              if (fullTypeName.indexOf(":") > -1) {
                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
              }
              nsPrefix = nsPrefix == null ? "" : nsPrefix;

              String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

              if (!"ArrayOfADDepartment".equals(type)) {
                // find namespace for the prefix
                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                return (ArrayOfADDepartment) ExtensionMapper.getTypeObject(nsUri, type, reader);
              }
            }
          }

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          reader.next();

          java.util.ArrayList list1 = new java.util.ArrayList();

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "ADDepartment")
                  .equals(reader.getName())) {

            // Process the array and step past its final element's end.

            nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
              list1.add(null);
              reader.next();
            } else {
              list1.add(ADDepartment.Factory.parse(reader));
            }
            // loop until we find a start element that is not part of this array
            boolean loopDone1 = false;
            while (!loopDone1) {
              // We should be at the end element, but make sure
              while (!reader.isEndElement()) reader.next();
              // Step out of this element
              reader.next();
              // Step to next element event.
              while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
              if (reader.isEndElement()) {
                // two continuous end elements means we are exiting the xml structure
                loopDone1 = true;
              } else {
                if (new javax.xml.namespace.QName("http://tempuri.org/", "ADDepartment")
                    .equals(reader.getName())) {

                  nillableValue =
                      reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                  if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                    list1.add(null);
                    reader.next();
                  } else {
                    list1.add(ADDepartment.Factory.parse(reader));
                  }
                } else {
                  loopDone1 = true;
                }
              }
            }
            // call the converter utility  to convert and set the array

            object.setADDepartment(
                (ADDepartment[])
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                        ADDepartment.class, list1));

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement())
            // 2 - A start element we are not expecting indicates a trailing invalid property

            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  public static class GetUserListResponse implements org.apache.axis2.databinding.ADBBean {

    public static final javax.xml.namespace.QName MY_QNAME =
        new javax.xml.namespace.QName("http://tempuri.org/", "GetUserListResponse", "ns1");

    /** field for GetUserListResult */
    protected ArrayOfADUser localGetUserListResult;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localGetUserListResultTracker = false;

    public boolean isGetUserListResultSpecified() {
      return localGetUserListResultTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return ArrayOfADUser
     */
    public ArrayOfADUser getGetUserListResult() {
      return localGetUserListResult;
    }

    /**
     * Auto generated setter method
     *
     * @param param GetUserListResult
     */
    public void setGetUserListResult(ArrayOfADUser param) {
      localGetUserListResultTracker = param != null;

      this.localGetUserListResult = param;
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {

      return factory.createOMElement(
          new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME));
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
      serialize(parentQName, xmlWriter, false);
    }

    public void serialize(
        final javax.xml.namespace.QName parentQName,
        javax.xml.stream.XMLStreamWriter xmlWriter,
        boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

      String prefix = null;
      String namespace = null;

      prefix = parentQName.getPrefix();
      namespace = parentQName.getNamespaceURI();
      writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

      if (serializeType) {

        String namespacePrefix = registerPrefix(xmlWriter, "http://tempuri.org/");
        if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              namespacePrefix + ":GetUserListResponse",
              xmlWriter);
        } else {
          writeAttribute(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance",
              "type",
              "GetUserListResponse",
              xmlWriter);
        }
      }
      if (localGetUserListResultTracker) {
        if (localGetUserListResult == null) {
          throw new org.apache.axis2.databinding.ADBException("GetUserListResult cannot be null!!");
        }
        localGetUserListResult.serialize(
            new javax.xml.namespace.QName("http://tempuri.org/", "GetUserListResult"), xmlWriter);
      }
      xmlWriter.writeEndElement();
    }

    private static String generatePrefix(String namespace) {
      if (namespace.equals("http://tempuri.org/")) {
        return "ns1";
      }
      return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Utility method to write an element start tag. */
    private void writeStartElement(
        String prefix,
        String namespace,
        String localPart,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
      } else {
        if (namespace.length() == 0) {
          prefix = "";
        } else if (prefix == null) {
          prefix = generatePrefix(namespace);
        }

        xmlWriter.writeStartElement(prefix, localPart, namespace);
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
    }

    /** Util method to write an attribute with the ns prefix */
    private void writeAttribute(
        String prefix,
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String writerPrefix = xmlWriter.getPrefix(namespace);
      if (writerPrefix != null) {
        xmlWriter.writeAttribute(writerPrefix, namespace, attName, attValue);
      } else {
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
        xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeAttribute(
        String namespace,
        String attName,
        String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attValue);
      } else {
        xmlWriter.writeAttribute(
            registerPrefix(xmlWriter, namespace), namespace, attName, attValue);
      }
    }

    /** Util method to write an attribute without the ns prefix */
    private void writeQNameAttribute(
        String namespace,
        String attName,
        javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      String attributeNamespace = qname.getNamespaceURI();
      String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
      if (attributePrefix == null) {
        attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
      }
      String attributeValue;
      if (attributePrefix.trim().length() > 0) {
        attributeValue = attributePrefix + ":" + qname.getLocalPart();
      } else {
        attributeValue = qname.getLocalPart();
      }

      if (namespace.equals("")) {
        xmlWriter.writeAttribute(attName, attributeValue);
      } else {
        registerPrefix(xmlWriter, namespace);
        xmlWriter.writeAttribute(attributePrefix, namespace, attName, attributeValue);
      }
    }
    /** method to handle Qnames */
    private void writeQName(
        javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
      String namespaceURI = qname.getNamespaceURI();
      if (namespaceURI != null) {
        String prefix = xmlWriter.getPrefix(namespaceURI);
        if (prefix == null) {
          prefix = generatePrefix(namespaceURI);
          xmlWriter.writeNamespace(prefix, namespaceURI);
          xmlWriter.setPrefix(prefix, namespaceURI);
        }

        if (prefix.trim().length() > 0) {
          xmlWriter.writeCharacters(
              prefix
                  + ":"
                  + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        } else {
          // i.e this is the default namespace
          xmlWriter.writeCharacters(
              org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }

      } else {
        xmlWriter.writeCharacters(
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }
    }

    private void writeQNames(
        javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

      if (qnames != null) {
        // we have to store this data until last moment since it is not possible to write any
        // namespace data after writing the charactor data
        StringBuffer stringToWrite = new StringBuffer();
        String namespaceURI = null;
        String prefix = null;

        for (int i = 0; i < qnames.length; i++) {
          if (i > 0) {
            stringToWrite.append(" ");
          }
          namespaceURI = qnames[i].getNamespaceURI();
          if (namespaceURI != null) {
            prefix = xmlWriter.getPrefix(namespaceURI);
            if ((prefix == null) || (prefix.length() == 0)) {
              prefix = generatePrefix(namespaceURI);
              xmlWriter.writeNamespace(prefix, namespaceURI);
              xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
              stringToWrite
                  .append(prefix)
                  .append(":")
                  .append(
                      org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            } else {
              stringToWrite.append(
                  org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
            }
          } else {
            stringToWrite.append(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        }
        xmlWriter.writeCharacters(stringToWrite.toString());
      }
    }

    /** Register a namespace prefix */
    private String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, String namespace)
        throws javax.xml.stream.XMLStreamException {
      String prefix = xmlWriter.getPrefix(namespace);
      if (prefix == null) {
        prefix = generatePrefix(namespace);
        javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
        while (true) {
          String uri = nsContext.getNamespaceURI(prefix);
          if (uri == null || uri.length() == 0) {
            break;
          }
          prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }
        xmlWriter.writeNamespace(prefix, namespace);
        xmlWriter.setPrefix(prefix, namespace);
      }
      return prefix;
    }

    /** Factory class that keeps the parse method */
    public static class Factory {
      private static org.apache.commons.logging.Log log =
          org.apache.commons.logging.LogFactory.getLog(Factory.class);

      /**
       * static method to create the object Precondition: If this object is an element, the current
       * or next start element starts this object and any intervening reader events are ignorable If
       * this object is not an element, it is a complex type and the reader is at the event just
       * after the outer start element Postcondition: If this object is an element, the reader is
       * positioned at its end element If this object is a complex type, the reader is positioned at
       * the end element of its outer element
       */
      public static GetUserListResponse parse(javax.xml.stream.XMLStreamReader reader)
          throws Exception {
        GetUserListResponse object = new GetUserListResponse();

        int event;
        javax.xml.namespace.QName currentQName = null;
        String nillableValue = null;
        String prefix = "";
        String namespaceuri = "";
        try {

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          currentQName = reader.getName();

          if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type")
              != null) {
            String fullTypeName =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (fullTypeName != null) {
              String nsPrefix = null;
              if (fullTypeName.indexOf(":") > -1) {
                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
              }
              nsPrefix = nsPrefix == null ? "" : nsPrefix;

              String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

              if (!"GetUserListResponse".equals(type)) {
                // find namespace for the prefix
                String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                return (GetUserListResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
              }
            }
          }

          // Note all attributes that were handled. Used to differ normal attributes
          // from anyAttributes.
          java.util.Vector handledAttributes = new java.util.Vector();

          reader.next();

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement()
              && new javax.xml.namespace.QName("http://tempuri.org/", "GetUserListResult")
                  .equals(reader.getName())) {

            object.setGetUserListResult(ArrayOfADUser.Factory.parse(reader));

            reader.next();

          } // End of if for expected property start element
          else {

          }

          while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

          if (reader.isStartElement())
            // 2 - A start element we are not expecting indicates a trailing invalid property

            throw new org.apache.axis2.databinding.ADBException(
                "Unexpected subelement " + reader.getName());

        } catch (javax.xml.stream.XMLStreamException e) {
          throw new Exception(e);
        }

        return object;
      }
    } // end of factory class
  }

  private org.apache.axiom.om.OMElement toOM(
      GetUserList param, boolean optimizeContent)
      throws org.apache.axis2.AxisFault {

    try {
      return param.getOMElement(
          GetUserList.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private org.apache.axiom.om.OMElement toOM(
      GetUserListResponse param,
      boolean optimizeContent)
      throws org.apache.axis2.AxisFault {

    try {
      return param.getOMElement(
          GetUserListResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private org.apache.axiom.om.OMElement toOM(
      ESBSecurityTokenE param,
      boolean optimizeContent)
      throws org.apache.axis2.AxisFault {

    try {
      return param.getOMElement(
          ESBSecurityTokenE.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private org.apache.axiom.om.OMElement toOM(
      GetDepartMentList param,
      boolean optimizeContent)
      throws org.apache.axis2.AxisFault {

    try {
      return param.getOMElement(
          GetDepartMentList.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private org.apache.axiom.om.OMElement toOM(
      GetDepartMentListResponse param,
      boolean optimizeContent)
      throws org.apache.axis2.AxisFault {

    try {
      return param.getOMElement(
          GetDepartMentListResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    } catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
      org.apache.axiom.soap.SOAPFactory factory,
      GetUserList param,
      boolean optimizeContent,
      javax.xml.namespace.QName elementQName)
      throws org.apache.axis2.AxisFault {

    try {

      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope
          .getBody()
          .addChild(
              param.getOMElement(
                  GetUserList.MY_QNAME, factory));
      return emptyEnvelope;
    } catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  /* methods to provide back word compatibility */

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
      org.apache.axiom.soap.SOAPFactory factory,
      GetDepartMentList param,
      boolean optimizeContent,
      javax.xml.namespace.QName elementQName)
      throws org.apache.axis2.AxisFault {

    try {

      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
      emptyEnvelope
          .getBody()
          .addChild(
              param.getOMElement(
                  GetDepartMentList.MY_QNAME,
                  factory));
      return emptyEnvelope;
    } catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  /* methods to provide back word compatibility */

  /** get the default envelope */
  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory) {
    return factory.getDefaultEnvelope();
  }

  private Object fromOM(org.apache.axiom.om.OMElement param, Class type)
      throws org.apache.axis2.AxisFault {

    try {

      if (ESBSecurityTokenE.class.equals(type)) {

        javax.xml.stream.XMLStreamReader reader = param.getXMLStreamReaderWithoutCaching();
        Object result =
            ESBSecurityTokenE.Factory.parse(reader);
        reader.close();
        return result;
      }

      if (GetDepartMentList.class.equals(type)) {

        javax.xml.stream.XMLStreamReader reader = param.getXMLStreamReaderWithoutCaching();
        Object result =
            GetDepartMentList.Factory.parse(reader);
        reader.close();
        return result;
      }

      if (GetDepartMentListResponse.class.equals(
          type)) {

        javax.xml.stream.XMLStreamReader reader = param.getXMLStreamReaderWithoutCaching();
        Object result =
            GetDepartMentListResponse.Factory.parse(
                reader);
        reader.close();
        return result;
      }

      if (GetUserList.class.equals(type)) {

        javax.xml.stream.XMLStreamReader reader = param.getXMLStreamReaderWithoutCaching();
        Object result =
            GetUserList.Factory.parse(reader);
        reader.close();
        return result;
      }

      if (GetUserListResponse.class.equals(type)) {

        javax.xml.stream.XMLStreamReader reader = param.getXMLStreamReaderWithoutCaching();
        Object result =
            GetUserListResponse.Factory.parse(reader);
        reader.close();
        return result;
      }

    } catch (Exception e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
    return null;
  }
}
