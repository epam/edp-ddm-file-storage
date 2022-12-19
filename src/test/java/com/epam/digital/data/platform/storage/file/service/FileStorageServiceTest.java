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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.integration.ceph.model.CephObjectMetadata;
import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.epam.digital.data.platform.storage.file.dto.BaseFileMetadataDto;
import com.epam.digital.data.platform.storage.file.dto.BaseFileMetadataDto.BaseUserMetadataHeaders;
import com.epam.digital.data.platform.storage.file.dto.FileObjectDto;
import com.epam.digital.data.platform.storage.file.repository.FileRepositoryImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

  private final String key = "key";
  private final String bucket = "bucket";
  private final String contentType = "contentType";
  private final String processInstanceId = "processInstanceId";
  private final String fileId = "fileId";
  private final long contentLength = 5L;
  private Map<String, String> userMetadata;
  private CephObjectMetadata metadata;

  @Mock
  private CephService cephService;
  private FileStorageService instance;

  @BeforeEach
  void init() {
    var keyProvider = new FormDataFileKeyProviderImpl();
    var repository = FileRepositoryImpl.builder()
        .cephBucketName(bucket)
        .cephService(cephService)
        .build();
    instance = FileStorageService.builder()
        .keyProvider(keyProvider)
        .repository(repository)
        .build();
    userMetadata = new HashMap<>();
    userMetadata.put("checksum", "sha256");
    userMetadata.put(BaseUserMetadataHeaders.ID, key);
    metadata = new CephObjectMetadata(contentLength, contentType, userMetadata);
  }

  @Test
  void shouldPassRightArgumentsToCephService() {
    when(cephService.put(eq(bucket), eq(key), eq(contentType), eq(contentLength), eq(userMetadata),
        any(InputStream.class))).thenReturn(metadata);

    var result = instance.save(key, content());

    assertThat(result.getContentLength()).isEqualTo(contentLength);
    assertThat(result.getContentType()).isEqualTo(contentType);
    assertThat(result.getUserMetadata()).isEqualTo(userMetadata);
    assertThat(result.getFilename()).isNull();
    assertThat(result.getId()).isEqualTo(key);
  }

  @Test
  void shouldGenerateKeyUsingKeyProvider() {
    var generatedKey = "process/processInstanceId/fileId";
    userMetadata.put(BaseUserMetadataHeaders.ID, generatedKey);
    when(cephService.put(eq(bucket), eq(generatedKey), eq(contentType), eq(contentLength),
        eq(userMetadata),
        any(InputStream.class))).thenReturn(metadata);

    var result = instance.save(processInstanceId, fileId, content());

    assertThat(result.getContentLength()).isEqualTo(contentLength);
    assertThat(result.getContentType()).isEqualTo(contentType);
    assertThat(result.getUserMetadata()).isEqualTo(userMetadata);
    assertThat(result.getFilename()).isNull();
    assertThat(result.getId()).isEqualTo(generatedKey);
  }

  @Test
  void shouldSetUserMetadataByKey() {
    when(cephService.setUserMetadata(bucket, key, userMetadata)).thenReturn(metadata);

    var result = instance.setUserMetadata(key, userMetadata);

    assertThat(result.getContentLength()).isEqualTo(contentLength);
    assertThat(result.getContentType()).isEqualTo(contentType);
    assertThat(result.getUserMetadata()).isEqualTo(userMetadata);
    assertThat(result.getFilename()).isNull();
    assertThat(result.getId()).isEqualTo(key);
  }

  @Test
  void shouldGenerateKeyUsingKeyProviderToSetUserMetadata() {
    var generatedKey = "process/processInstanceId/fileId";
    userMetadata.put(BaseUserMetadataHeaders.ID, generatedKey);
    when(cephService.setUserMetadata(bucket, key, userMetadata)).thenReturn(metadata);

    var result = instance.setUserMetadata(key, userMetadata);

    assertThat(result.getContentLength()).isEqualTo(contentLength);
    assertThat(result.getContentType()).isEqualTo(contentType);
    assertThat(result.getUserMetadata()).isEqualTo(userMetadata);
    assertThat(result.getFilename()).isNull();
    assertThat(result.getId()).isEqualTo(generatedKey);
  }

  private FileObjectDto content() {
    var metadata = new BaseFileMetadataDto(contentLength, contentType, userMetadata);
    var content = new ByteArrayInputStream(new byte[]{'H', 'e', 'l', 'l', 'o'});
    return FileObjectDto.builder()
        .content(content)
        .metadata(metadata)
        .build();
  }
}