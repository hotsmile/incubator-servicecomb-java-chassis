/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.cse.springboot.starter.discovery;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

import com.huawei.paas.cse.core.provider.consumer.ConsumerProviderManager;
import com.huawei.paas.cse.core.provider.consumer.ReferenceConfig;
import com.netflix.config.DynamicPropertyFactory;

/**
 * @author Sukesh
 */
public final class CseRoutesProperties {

    @Inject
    private ConsumerProviderManager consumerProviderManager;

    @Autowired
    private ZuulProperties zuulProperties;

    private final Map<String, String> appServiceMap =  new HashMap<String, String>();

    private void loadZuulRoutes() {
        Map<String, ZuulRoute> zuulrouteMap = zuulProperties.getRoutes();
        for (String key : zuulrouteMap.keySet()) {
            appServiceMap.put(key, zuulrouteMap.get(key).getServiceId());
        }
    }

    public String getServiceName(String appID) {
        if (appServiceMap.isEmpty()) {
            loadZuulRoutes();
        }
        String serviceName = appServiceMap.get(appID);
        if (null == serviceName || serviceName.trim().isEmpty()) {
            serviceName = DynamicPropertyFactory.getInstance()
                    .getStringProperty("service_description.name", "default")
                    .get();
        }
        return serviceName;
    }

    public String getVersionRule(String serviceName) {
        ReferenceConfig referenceConfig = consumerProviderManager.getReferenceConfig(serviceName);
        String versionRule = referenceConfig.getMicroserviceVersionRule();
        return versionRule;
    }

    public String getAppID() {
        return DynamicPropertyFactory.getInstance().getStringProperty("APPLICATION_ID", "default").get();
    }

}
