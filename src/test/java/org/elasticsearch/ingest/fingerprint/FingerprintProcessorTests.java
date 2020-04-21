/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.ingest.fingerprint;

import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.RandomDocumentPicks;
import org.elasticsearch.test.ESTestCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;

public class FingerprintProcessorTests extends ESTestCase {

    private FingerprintProcessor processor;

    public void testMD5() {
        Set<String> fields = new HashSet<>();
        fields.add("_field");
        processor = new FingerprintProcessor(randomAlphaOfLength(10), fields, "fingerprint",
                FingerprintProcessor.Method.MD5, false, false, false);
        Map<String, Object> document = new HashMap<>();
        document.put("_field", "test content");
        IngestDocument oldDoc = RandomDocumentPicks.randomIngestDocument(random(), document);
        IngestDocument newDoc = processor.execute(oldDoc);
        assertEquals("a0a003767ebde642b4d908ef7417d8e5", newDoc.getFieldValue("fingerprint", String.class));
    }

    public void testMD5Base64() {
        Set<String> fields = new HashSet<>();
        fields.add("_field");
        processor = new FingerprintProcessor(randomAlphaOfLength(10), fields, "fingerprint",
                FingerprintProcessor.Method.MD5, true, false, false);
        Map<String, Object> document = new HashMap<>();
        document.put("_field", "test content");
        IngestDocument oldDoc = RandomDocumentPicks.randomIngestDocument(random(), document);
        IngestDocument newDoc = processor.execute(oldDoc);
        assertEquals("oKADdn695kK02QjvdBfY5Q==", newDoc.getFieldValue("fingerprint", String.class));
    }

    public void testSHA1Base64() {
        Set<String> fields = new HashSet<>();
        fields.add("_field");
        processor = new FingerprintProcessor(randomAlphaOfLength(10), fields, "fingerprint",
                FingerprintProcessor.Method.SHA1, true, false, false);
        Map<String, Object> document = new HashMap<>();
        document.put("_field", "test content");
        IngestDocument oldDoc = RandomDocumentPicks.randomIngestDocument(random(), document);
        IngestDocument newDoc = processor.execute(oldDoc);
        assertEquals("LNYh2CsbjGHNxZS2OfiQ9xrPZNo=", newDoc.getFieldValue("fingerprint", String.class));
    }

    public void testSHA256Base64() {
        Set<String> fields = new HashSet<>();
        fields.add("_field");
        processor = new FingerprintProcessor(randomAlphaOfLength(10), fields, "fingerprint",
                FingerprintProcessor.Method.SHA256, true, false, false);
        Map<String, Object> document = new HashMap<>();
        document.put("_field", "test content");
        IngestDocument oldDoc = RandomDocumentPicks.randomIngestDocument(random(), document);
        IngestDocument newDoc = processor.execute(oldDoc);
        assertEquals("34grIrfHH6eL3FDjLTnG/b30L2VYV2oxl1fER7Epbsg=", newDoc.getFieldValue("fingerprint", String.class));
    }

    public void testMURMUR3() {
        Set<String> fields = new HashSet<>();
        fields.add("_field");
        processor = new FingerprintProcessor(randomAlphaOfLength(10), fields, "fingerprint",
                FingerprintProcessor.Method.MURMUR3, false, false, false);
        Map<String, Object> document = new HashMap<>();
        document.put("_field", "test content");
        IngestDocument oldDoc = RandomDocumentPicks.randomIngestDocument(random(), document);
        IngestDocument newDoc = processor.execute(oldDoc);
        assertEquals("155d94baf6c2bf3ca5bc0c22c67f3158", newDoc.getFieldValue("fingerprint", String.class));
    }

    public void testMURMUR3Base64() {
        Set<String> fields = new HashSet<>();
        fields.add("_field");
        processor = new FingerprintProcessor(randomAlphaOfLength(10), fields, "fingerprint",
                FingerprintProcessor.Method.MURMUR3, true, false, false);
        Map<String, Object> document = new HashMap<>();
        document.put("_field", "test content");
        IngestDocument oldDoc = RandomDocumentPicks.randomIngestDocument(random(), document);
        IngestDocument newDoc = processor.execute(oldDoc);
        assertEquals("FV2UuvbCvzylvAwixn8xWA==", newDoc.getFieldValue("fingerprint", String.class));
    }

    public void testConcatenateAllFields() {
        processor = new FingerprintProcessor(randomAlphaOfLength(10), null, "fingerprint",
                FingerprintProcessor.Method.MD5, true, true, false);
        Map<String, Object> document = new HashMap<>();
        document.put("_field", "test content");

        IngestDocument oldDoc = new IngestDocument("my_index", null, null, null, null, null, document);
        IngestDocument newDoc = processor.execute(oldDoc);
        assertEquals("uFrwiqZ04cxCoqZOuTztSg==", newDoc.getFieldValue("fingerprint", String.class));

        // Changing meta-field 'id' and fingerprint will also be changed
        oldDoc = new IngestDocument("my_index", null, "1", null, null, null, document);
        newDoc = processor.execute(oldDoc);
        assertNotEquals("XAT5+Ih2t2i6exqpH0fo9w==", newDoc.getFieldValue("fingerprint", String.class));
    }

    public void testIgnoreMissing() throws Exception {
        Set<String> fields = new HashSet<>();
        fields.add("_field");
        fields.add("_field2");
        processor = new FingerprintProcessor(randomAlphaOfLength(10), fields, "fingerprint",
                FingerprintProcessor.Method.MD5, true, false, true);
        Map<String, Object> document = new HashMap<>();
        document.put("_field", "test content");
        IngestDocument oldDoc = RandomDocumentPicks.randomIngestDocument(random(), document);
        IngestDocument newDoc = processor.execute(oldDoc);
        assertEquals("oKADdn695kK02QjvdBfY5Q==", newDoc.getFieldValue("fingerprint", String.class));
    }

    public void testNullValueWithoutIgnoreMissing() throws Exception {
        Set<String> fields = new HashSet<>();
        fields.add("_field");
        processor = new FingerprintProcessor(randomAlphaOfLength(10), fields, "fingerprint",
                FingerprintProcessor.Method.MD5, false, false, false);
        Map<String, Object> document = new HashMap<>();
        document.put("_field", null);
        IngestDocument ingestDocument = RandomDocumentPicks.randomIngestDocument(random(), document);
        Exception exception = expectThrows(IllegalArgumentException.class, () -> processor.execute(ingestDocument));
        assertThat(exception.getMessage(), equalTo("field [_field] is null, cannot generate fingerprint from it."));
    }

    public void testNonExistedFieldWithoutIgnoreMissing() throws Exception {
        Set<String> fields = new HashSet<>();
        fields.add("_field");
        processor = new FingerprintProcessor(randomAlphaOfLength(10), fields, "fingerprint",
                FingerprintProcessor.Method.MD5, false, false, false);
        Map<String, Object> document = new HashMap<>();
        IngestDocument ingestDocument = RandomDocumentPicks.randomIngestDocument(random(), document);
        Exception exception = expectThrows(IllegalArgumentException.class, () -> processor.execute(ingestDocument));
        assertThat(exception.getMessage(), equalTo("field [_field] not present as part of path [_field]"));
    }

}
