/*
 * Copyright 1998-2012 Linux.org.ru
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.org.linux.spring;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Конфигурация
 */
@Service
public class Configuration {
  private static final String ERR_MSG = "Invalid MainUrl property: ";
  public static final String PROPERTY_MAIN_URL = "MainUrl";

  @Qualifier("properties")
  @Autowired
  private Properties properties;

  private URI mainURI;
  private URI secureURI;

  /**
   * Предполагается, что на этапе запуска приожения, если с MainUrl что-то не так то контейнер не запустится :-)
   */
  @PostConstruct
  public void init() {
    try {
      mainURI = new URI(properties.getProperty("MainUrl"), true, "UTF-8");
    } catch (Exception e) {
      throw new RuntimeException(ERR_MSG +e.getMessage());
    }
    if(!mainURI.isAbsoluteURI()) {
      throw new RuntimeException(ERR_MSG +"URI not absolute path");
    }

    try {
      String mainHost = mainURI.getHost();
      if(mainHost == null) {
        throw new RuntimeException(ERR_MSG +"bad URI host");
      }
    } catch (URIException e) {
     throw new RuntimeException(ERR_MSG +e.getMessage());
    }

    try {
      secureURI = new URI(properties.getProperty("SecureUrl", mainURI.toString().replaceFirst("http", "https")), true, "UTF-8");
    } catch (Exception e) {
      throw new RuntimeException(ERR_MSG +e.getMessage());
    }
  }

  public String getMainUrl() {
    return mainURI.toString();
  }

  public String getSecureUrl() {
    return secureURI.toString();
  }

  public URI getMainURI() {
    return mainURI;
  }

  public String getPathPrefix() {
    return properties.getProperty("PathPrefix");
  }

  public String getHTMLPathPrefix() {
    return properties.getProperty("HTMLPathPrefix");
  }

  public String getSecret() {
    return properties.getProperty("Secret");
  }
  public String getAdminEmailAddress() {
    return properties.getProperty("admin.emailAddress");
  }
}
