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

package com.epam.digital.data.platform.storage.file.sevice;

import com.epam.digital.data.platform.integration.ceph.model.CephObject;
import com.epam.digital.data.platform.integration.ceph.model.CephObjectMetadata;
import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.epam.digital.data.platform.storage.file.dto.FileDataDto;
import com.epam.digital.data.platform.storage.file.dto.FileMetadataDto;
import com.epam.digital.data.platform.storage.file.exception.FileNotFoundException;
import com.epam.digital.data.platform.storage.file.repository.CephFormDataFileRepository;
import com.epam.digital.data.platform.storage.file.service.FormDataFileKeyProvider;
import com.epam.digital.data.platform.storage.file.service.FormDataFileKeyProviderImpl;
import com.epam.digital.data.platform.storage.file.service.FormDataFileStorageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormDataFileStorageServiceTest {

  private final String bucketName = "bucket";

  @Mock
  private CephService cephService;
  private FormDataFileKeyProvider keyProvider;
  private FormDataFileStorageService fileStorageService;

  @BeforeEach
  void init() {
    var repository = CephFormDataFileRepository.builder()
        .cephBucketName(bucketName)
        .cephService(cephService)
        .build();
    keyProvider = new FormDataFileKeyProviderImpl();
    fileStorageService = FormDataFileStorageService.builder()
        .keyProvider(keyProvider)
        .repository(repository)
        .build();
  }

  @Test
  @SneakyThrows
  void testGetByFileIdAndProcessInstanceId() {
    var fileId = "fileId";
    var procInstId = "procInstId";
    var contentType = "application/png";
    var content = "content";
    var contentLength = 100L;
    var key = keyProvider.generateKey(procInstId, fileId);
    var cephObject = CephObject.builder()
        .content(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)))
        .metadata(CephObjectMetadata.builder()
            .contentLength(contentLength)
            .contentType(contentType)
            .build())
        .build();

    when(cephService.get(bucketName, key)).thenReturn(Optional.of(cephObject));

    var result = fileStorageService.loadByProcessInstanceIdAndId(procInstId, fileId);

    assertThat(result).isNotNull();
    assertThat(result.getMetadata().getContentType()).isEqualTo(contentType);
    assertThat(result.getMetadata().getContentLength()).isEqualTo(contentLength);
    assertThat(new String(result.getContent().readAllBytes())).isEqualTo(content);
  }

  @Test
  void testFileNotFound() {
    var key = "key";

    when(cephService.get(bucketName, key)).thenReturn(Optional.empty());

    var exception = assertThrows(FileNotFoundException.class,
        () -> fileStorageService.loadByKey(key));

    assertThat(exception.getIds().iterator().next()).isEqualTo(key);
  }

  @Test
  void testPutFile() {
    var fileId = "fileId";
    var procInstId = "procInstId";
    var key = keyProvider.generateKey(procInstId, fileId);
    var contentType = "application/png";
    var contentLength = 100L;
    var content = "content";
    var contentBytes = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    var userMetadata = Map.of("test", "test");
    var fileData = FileDataDto.builder()
        .content(contentBytes)
        .metadata(new FileMetadataDto(contentType, userMetadata))
        .build();

    var metadata = CephObjectMetadata.builder()
        .contentLength(contentLength)
        .userMetadata(userMetadata)
        .contentType(contentType)
        .build();
    when(cephService.put(bucketName, key, contentType, userMetadata, contentBytes)).thenReturn(
        metadata);

    var result = fileStorageService.save(procInstId, fileId, fileData);

    assertThat(result).isNotNull();
    assertThat(result.getContentType()).isEqualTo(contentType);
    assertThat(result.getContentLength()).isEqualTo(contentLength);
    assertThat(result.getUserMetadata()).isEqualTo(userMetadata);
  }

  @Test
  void testGetMetadata() {
    var processInstId = "processInstId";
    var fileId = "fileId";
    var contentLength = 100L;
    var contentType = "application/jpeg";
    var fileIds = Set.of(fileId);
    var metadata = CephObjectMetadata.builder()
        .contentLength(contentLength)
        .contentType(contentType)
        .build();

    when(cephService.getMetadata(bucketName,
        Set.of(keyProvider.generateKey(processInstId, fileId)))).thenReturn(List.of(metadata));

    var result = fileStorageService.getMetadata(processInstId, fileIds);

    assertThat(result.size()).isNotZero();
    assertThat(result.get(0).getContentType()).isEqualTo(contentType);
    assertThat(result.get(0).getContentLength()).isEqualTo(contentLength);
  }

  @Test
  void testGetMetadataByPrefix() {
    var processInstId = "processInstId";
    var metadata = CephObjectMetadata.builder()
        .userMetadata(Map.of("fieldName", "documents",
            "formKey", "form_key"))
        .build();

    when(cephService.getMetadata(bucketName,
        keyProvider.getKeyPrefixByProcessInstanceId(processInstId))).thenReturn(List.of(metadata));

    var result = fileStorageService.getMetadata(processInstId);

    assertThat(result.size()).isNotZero();
    assertThat(result.get(0).getFieldName()).isEqualTo("documents");
    assertThat(result.get(0).getFormKey()).isEqualTo("form_key");
  }

  @Test
  void testDeleteByProcInstId() {
    var procInstId = "id";
    var prefix = keyProvider.getKeyPrefixByProcessInstanceId(procInstId);
    var key = keyProvider.generateKey(procInstId, "uuid");

    when(cephService.getKeys(bucketName, prefix)).thenReturn(Set.of(key));

    fileStorageService.deleteByProcessInstanceId(procInstId);

    verify(cephService).delete(bucketName, Set.of(key));
  }

  @Test
  void testShouldThrowFileNotFoundWithCorrectMsg() {
    var procInstId = "procInsId";
    var documentId = "docId";

    when(cephService.get(any(), any())).thenReturn(Optional.empty());

    var exception = assertThrows(FileNotFoundException.class,
        () -> fileStorageService.loadByProcessInstanceIdAndId(procInstId, documentId));

    assertThat(exception.getIds().iterator().next()).isEqualTo(documentId);
  }
}