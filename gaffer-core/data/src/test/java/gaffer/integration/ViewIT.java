/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaffer.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import gaffer.commonutil.StreamUtil;
import gaffer.commonutil.TestGroups;
import gaffer.commonutil.TestPropertyNames;
import gaffer.data.element.ElementComponentKey;
import gaffer.data.element.IdentifierType;
import gaffer.data.element.function.ElementTransformer;
import gaffer.data.elementdefinition.view.View;
import gaffer.data.elementdefinition.view.ViewElementDefinition;
import gaffer.function.ExampleTransformFunction;
import gaffer.function.TransformFunction;
import gaffer.function.context.ConsumerProducerFunctionContext;
import org.junit.Test;
import java.io.IOException;
import java.util.List;


public class ViewIT {
    @Test
    public void shouldDeserialiseJsonView() throws IOException {
        // Given

        // When
        View view = loadView();

        // Then
        final ViewElementDefinition edge = view.getEdge(TestGroups.EDGE);
        final ElementTransformer transformer = edge.getTransformer();
        assertNotNull(transformer);

        final List<ConsumerProducerFunctionContext<ElementComponentKey, TransformFunction>> contexts = transformer.getFunctions();
        assertEquals(1, contexts.size());

        final List<ElementComponentKey> selection = contexts.get(0).getSelection();
        assertEquals(2, selection.size());
        assertEquals(TestPropertyNames.F1, selection.get(0).getPropertyName());
        assertEquals(IdentifierType.SOURCE, selection.get(1).getIdentifierType());

        final List<ElementComponentKey> projection = contexts.get(0).getProjection();
        assertEquals(1, projection.size());
        assertEquals("concatProperty", projection.get(0).getPropertyName());

        assertTrue(contexts.get(0).getFunction() instanceof ExampleTransformFunction);
    }

    @Test
    public void shouldValidateJsonViewAndPass() throws IOException {
        // Given
        View view = loadView();

        // When
        boolean response = view.validate();

        // Then
        assertTrue(response);
    }

    @Test
    public void shouldValidateJsonViewAndFail() throws IOException {
        // Given
        View view = loadInvalidView();

        // When
        boolean response = view.validate();

        //then
        assertFalse(response);
    }

    @Test
    public void shouldDeserialiseAndReserialiseIntoTheSameJson() throws IOException {
        //Given
        final View view1 = loadView();
        final byte[] json1 = view1.toJson(false);
        final View view2 = View.fromJson(json1);

        // When
        final byte[] json2 = view2.toJson(false);

        // Then
        assertEquals(new String(json1), new String(json2));
    }

    @Test
    public void shouldDeserialiseAndReserialiseIntoTheSamePrettyJson() throws IOException {
        //Given
        final View view1 = loadView();
        final byte[] json1 = view1.toJson(true);
        final View view2 = View.fromJson(json1);

        // When
        final byte[] json2 = view2.toJson(true);

        // Then
        assertEquals(new String(json1), new String(json2));
    }

    private View loadView() throws IOException {
        return View.fromJson(StreamUtil.view(getClass()));
    }

    private View loadInvalidView() throws IOException {
        return View.fromJson(StreamUtil.openStream(getClass(), "/invalidView.json"));
    }
}
