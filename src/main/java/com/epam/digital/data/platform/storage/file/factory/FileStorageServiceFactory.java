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

package com.epam.digital.data.platform.storage.file.factory;

import com.epam.digital.data.platform.integration.ceph.factory.CephS3Factory;
import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.epam.digital.data.platform.storage.file.config.FileDataCephStorageConfiguration;
import com.epam.digital.data.platform.storage.file.repository.FileRepositoryImpl;
import com.epam.digital.data.platform.storage.file.service.FileStorageService;
import com.epam.digital.data.platform.storage.file.service.FormDataFileKeyProvider;
import com.epam.digital.data.platform.storage.file.service.FormDataFileKeyProviderImpl;
import lombok.RequiredArgsConstructor;

/**
 * The class for creation storage services based on supported configuration
 */
@RequiredArgsConstructor
public class FileStorageServiceFactory {

  private final CephS3Factory cephFactory;

  public FileStorageService fileStorageService(FileDataCephStorageConfiguration config) {
    return FileStorageService.builder()
        .repository(newFileRepository(config))
        .keyProvider(newFormDataFileKeyProvider())
        .build();
  }

  public FileRepositoryImpl newFileRepository(FileDataCephStorageConfiguration config) {
    return FileRepositoryImpl.builder()
        .cephBucketName(config.getBucket())
        .cephService(newCephServiceS3(config))
        .build();
  }

  private FormDataFileKeyProvider newFormDataFileKeyProvider() {
    return new FormDataFileKeyProviderImpl();
  }

  private CephService newCephServiceS3(FileDataCephStorageConfiguration config) {
    return cephFactory.createCephService(config.getHttpEndpoint(),
        config.getAccessKey(), config.getSecretKey());
  }
}
