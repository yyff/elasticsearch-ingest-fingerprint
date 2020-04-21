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

import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.test.ESTestCase;

public class FingerprintProcessorFactoryTests extends ESTestCase {

    private FingerprintProcessor.Factory factory = new FingerprintProcessor.Factory();

    public void testBuildDefaults() throws Exception {
        Map<String, Object> config = new HashMap<>();
        List<String> fieldList = new ArrayList<>();
        fieldList.add("_field");
        config.put("fields", fieldList);
        String processorTag = randomAlphaOfLength(10);

        FingerprintProcessor processor = factory.create(null, processorTag, config);
        assertEquals(processorTag, processor.getTag());
        assertEquals(FingerprintProcessor.TYPE, processor.getType());
        Set<String> expectFeilds = new HashSet<>();
        expectFeilds.add("_field");
        assertEquals(expectFeilds, processor.getFields());
        assertEquals("fingerprint", processor.getTargetField());
        assertEquals(FingerprintProcessor.Method.MD5, processor.getMethod());
        assertFalse(processor.isBase64Encode());
        assertFalse(processor.isConcatenateAllFields());
        assertFalse(processor.isIgnoreMissing());
    }

    public void testEmptyFieldsThrowException() throws Exception {

        Map<String, Object> config = new HashMap<>();
        String processorTag = randomAlphaOfLength(10);
        Exception exception = expectThrows(ElasticsearchParseException.class,
                () -> factory.create(null, processorTag, config));
        assertThat(exception.getMessage(), equalTo("[fields] can't be empty when 'concatenateAllFields' is false"));
    }

    public void testConcatenateAllFieldsWithoutFields() throws Exception {

        Map<String, Object> config = new HashMap<>();
        config.put("concatenate_all_fields", true);

        String processorTag = randomAlphaOfLength(10);
        FingerprintProcessor processor = factory.create(null, processorTag, config);
        assertEquals(processorTag, processor.getTag());
        assertEquals(FingerprintProcessor.TYPE, processor.getType());
        Set<String> expectFeilds = new HashSet<>();
        assertEquals(expectFeilds, processor.getFields());
        assertEquals("fingerprint", processor.getTargetField());
        assertEquals(FingerprintProcessor.Method.MD5, processor.getMethod());
        assertFalse(processor.isBase64Encode());
        assertTrue(processor.isConcatenateAllFields());
        assertFalse(processor.isIgnoreMissing());

    }

    public void testConcatenateAllFieldsWithFields() throws Exception {

        Map<String, Object> config = new HashMap<>();
        List<String> fieldList = new ArrayList<>();
        fieldList.add("_field");
        config.put("fields", fieldList);
        config.put("concatenate_all_fields", true);

        String processorTag = randomAlphaOfLength(10);
        FingerprintProcessor processor = factory.create(null, processorTag, config);
        assertEquals(processorTag, processor.getTag());
        assertEquals(FingerprintProcessor.TYPE, processor.getType());
        Set<String> expectFeilds = new HashSet<>();
        assertEquals(expectFeilds, processor.getFields());
        assertEquals("fingerprint", processor.getTargetField());
        assertEquals(FingerprintProcessor.Method.MD5, processor.getMethod());
        assertFalse(processor.isBase64Encode());
        assertTrue(processor.isConcatenateAllFields());
        assertFalse(processor.isIgnoreMissing());

    }

    public void testValidMethod() throws Exception {

        Map<String, Object> config = new HashMap<>();
        List<String> fieldList = new ArrayList<>();
        fieldList.add("_field");
        config.put("fields", fieldList);
        config.put("method", "SHA256");

        String processorTag = randomAlphaOfLength(10);
        FingerprintProcessor processor = factory.create(null, processorTag, config);
        assertEquals(processorTag, processor.getTag());
        assertEquals(FingerprintProcessor.TYPE, processor.getType());
        Set<String> expectFeilds = new HashSet<>();
        expectFeilds.add("_field");
        assertEquals(expectFeilds, processor.getFields());
        assertEquals("fingerprint", processor.getTargetField());
        assertEquals(FingerprintProcessor.Method.SHA256, processor.getMethod());
        assertFalse(processor.isBase64Encode());
        assertFalse(processor.isConcatenateAllFields());
        assertFalse(processor.isIgnoreMissing());

    }

    public void testInvalidMethod() throws Exception {

        Map<String, Object> config = new HashMap<>();
        List<String> fieldList = new ArrayList<>();
        fieldList.add("_field");
        config.put("fields", fieldList);
        config.put("method", "Invalid method");

        String processorTag = randomAlphaOfLength(10);
        Exception exception = expectThrows(ElasticsearchParseException.class,
                () -> factory.create(null, processorTag, config));
        assertThat(exception.getMessage(), equalTo(
                "[method] illegal method option [Invalid method]. valid values are [SHA1, SHA256, MD5, MURMUR3, UUID]"));

    }

    public void testBase64Encode() throws Exception {

        Map<String, Object> config = new HashMap<>();
        List<String> fieldList = new ArrayList<>();
        fieldList.add("_field");
        config.put("fields", fieldList);
        config.put("base64_encode", true);

        String processorTag = randomAlphaOfLength(10);
        FingerprintProcessor processor = factory.create(null, processorTag, config);
        assertEquals(processorTag, processor.getTag());
        assertEquals(FingerprintProcessor.TYPE, processor.getType());
        Set<String> expectFeilds = new HashSet<>();
        expectFeilds.add("_field");
        assertEquals(expectFeilds, processor.getFields());
        assertEquals("fingerprint", processor.getTargetField());
        assertEquals(FingerprintProcessor.Method.MD5, processor.getMethod());
        assertTrue(processor.isBase64Encode());
        assertFalse(processor.isConcatenateAllFields());
        assertFalse(processor.isIgnoreMissing());

    }

    public void testIgnoreMissing() throws Exception {
        Map<String, Object> config = new HashMap<>();
        List<String> fieldList = new ArrayList<>();
        fieldList.add("_field");
        config.put("fields", fieldList);
        config.put("ignore_missing", true);

        String processorTag = randomAlphaOfLength(10);
        FingerprintProcessor processor = factory.create(null, processorTag, config);
        assertEquals(processorTag, processor.getTag());
        assertEquals(FingerprintProcessor.TYPE, processor.getType());
        Set<String> expectFeilds = new HashSet<>();
        expectFeilds.add("_field");
        assertEquals(expectFeilds, processor.getFields());
        assertEquals("fingerprint", processor.getTargetField());
        assertEquals(FingerprintProcessor.Method.MD5, processor.getMethod());
        assertFalse(processor.isBase64Encode());
        assertFalse(processor.isConcatenateAllFields());
        assertTrue(processor.isIgnoreMissing());

    }
}
