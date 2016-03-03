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

package gaffer.data.element;

import gaffer.exception.SerialisationException;
import gaffer.jsonserialisation.JSONSerialiser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class EntityTest extends ElementTest {

    @Test
    public void shouldSetAndGetFields() {
        // Given
        final Entity entity = new Entity("group");

        // When
        entity.setVertex("identifier");

        // Then
        assertEquals("group", entity.getGroup());
        assertEquals("identifier", entity.getVertex());
    }

    @Test
    public void shouldReturnTrueForEqualsWithTheSameInstance() {
        // Given
        final Entity entity = new Entity("group");
        entity.setVertex("identifier");

        // When
        boolean isEqual = entity.equals(entity);

        // Then
        assertTrue(isEqual);
        assertEquals(entity.hashCode(), entity.hashCode());
    }

    @Test
    public void shouldReturnTrueForEqualsWhenAllCoreFieldsAreEqual() {
        // Given
        final Entity entity1 = new Entity("group");
        entity1.setVertex("identifier");
        entity1.putProperty("some property", "some value");

        final Entity entity2 = cloneCoreFields(entity1);
        entity2.putProperty("some different property", "some other value");

        // When
        boolean isEqual = entity1.equals((Object) entity2);

        // Then
        assertTrue(isEqual);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenGroupIsDifferent() {
        // Given
        final Entity entity1 = new Entity("group");
        entity1.setVertex("vertex");

        final Entity entity2 = new Entity("a different group");
        entity2.setVertex(entity1.getVertex());

        // When
        boolean isEqual = entity1.equals((Object) entity2);

        // Then
        assertFalse(isEqual);
        assertFalse(entity1.hashCode() == entity2.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenIdentifierIsDifferent() {
        // Given
        final Entity entity1 = new Entity("group");
        entity1.setVertex("vertex");

        final Entity entity2 = cloneCoreFields(entity1);
        entity2.setVertex("different vertex");

        // When
        boolean isEqual = entity1.equals((Object) entity2);

        // Then
        assertFalse(isEqual);
        assertFalse(entity1.hashCode() == entity2.hashCode());
    }

    @Test
    public void shouldSerialiseAndDeserialiseIdentifiers() throws SerialisationException {
        // Given
        final Entity entity = newElement("group");
        entity.setVertex(1L);

        final JSONSerialiser serialiser = new JSONSerialiser();

        // When
        final byte[] serialisedElement = serialiser.serialise(entity);
        final Entity deserialisedElement = serialiser.deserialise(serialisedElement, entity.getClass());

        // Then
        assertEquals(entity, deserialisedElement);
    }

    @Override
    protected Entity newElement(final String group) {
        return new Entity(group);
    }

    @Override
    protected Entity newElement() {
        return new Entity();
    }

    private Entity cloneCoreFields(final Entity entity) {
        final Entity newEntity = new Entity(entity.getGroup());
        newEntity.setVertex(entity.getVertex());

        return newEntity;
    }
}
