/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.epam.digital.data.platform.storage.file.service;

/**
 * The class represents a provider that is used to generate the key to get or store file form data.
 */
public interface FormDataFileKeyProvider {

  /**
   * Method for generating the key, uses document id and process instance identifier to construct
   * the key
   *
   * @param processInstanceId process instance identifier
   * @param documentId        document id
   * @return generated key
   */
  String generateKey(String processInstanceId, String documentId);

  /**
   * Get key prefix with specified process instance id
   *
   * @param processInstanceId specified process instance id
   * @return generated prefix
   */
  String getKeyPrefixByProcessInstanceId(String processInstanceId);
}
