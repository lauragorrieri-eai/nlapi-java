/*
 * Copyright (c) 2020 original authors
 *
 * Licensed under the Apache License, Versions 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.expert.nlapi.v1;

import ai.expert.nlapi.security.Authentication;
import ai.expert.nlapi.security.SecurityUtils;
import ai.expert.nlapi.utils.ObjectMapperAdapter;
import ai.expert.nlapi.v1.message.RequestDocument;
import ai.expert.nlapi.v1.message.ResponseDocument;
import kong.unirest.Unirest;

public class Categorizer {

    private final Authentication authentication;
    private final String URL;

    public Categorizer(CategorizerConfig config) {

        authentication = config.getAuthentication();
        URL = String.format("%s/%s/categorize/%s/%s", API.AUTHORITY, config.getVersion(), config.getTaxonomy(), config.getLanguage());

        Unirest.config()
               .addDefaultHeader("Content-Type", "application/json")
               .addDefaultHeader("Accept", "application/json")
               .setObjectMapper(new ObjectMapperAdapter());
    }

    public ResponseDocument categorize(String text) {

        return Unirest.post(URL)
                      .header("Authorization", getBearerToken())
                      .body(RequestDocument.of(text).toJSON())
                      .asObject(ResponseDocument.class)
                      .getBody();
    }

    public String categorizeAsString(String text) {

        return Unirest.post(URL)
                      .header("Authorization", getBearerToken())
                      .body(RequestDocument.of(text).toJSON())
                      .asString()
                      .getBody();
    }

    private String getBearerToken() {
        return SecurityUtils.bearerOf(authentication.isValid() ? authentication.getJWT() : authentication.refresh());
    }
}
