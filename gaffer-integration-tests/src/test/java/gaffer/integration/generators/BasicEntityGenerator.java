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
package gaffer.integration.generators;

import gaffer.commonutil.TestGroups;
import gaffer.commonutil.TestPropertyNames;
import gaffer.data.element.Element;
import gaffer.data.element.Entity;
import gaffer.data.generator.OneToOneElementGenerator;
import gaffer.integration.domain.EntityDomainObject;

/**
 * Implementation of {@link gaffer.data.generator.OneToOneElementGenerator} to translate between integration test 'edge'
 * object, and a Gaffer framework edge.
 * <br>
 * Allows translation of one domain object to one graph object only, where the domain object being translated is an instance
 * of {@link gaffer.integration.domain.EntityDomainObject}.  The generator can go both ways (i.e. domain object to graph element and
 * graph element to domain object).
 */
public class BasicEntityGenerator extends OneToOneElementGenerator<EntityDomainObject> {
    @Override
    public Element getElement(final EntityDomainObject domainObject) {
        final Entity entity = new Entity(TestGroups.ENTITY, domainObject.getName());
        entity.putProperty(TestPropertyNames.INT, domainObject.getIntProperty());
        entity.putProperty(TestPropertyNames.STRING, domainObject.getStringproperty());
        return entity;
    }

    @Override
    public EntityDomainObject getObject(final Element element) {
        if (element instanceof Entity) {
            final Entity entity = ((Entity) element);
            final EntityDomainObject basicEntity = new EntityDomainObject();
            basicEntity.setName((String) entity.getVertex());
            basicEntity.setIntProperty((Integer) entity.getProperty(TestPropertyNames.INT));
            basicEntity.setStringproperty((String) entity.getProperty(TestPropertyNames.STRING));
            return basicEntity;
        }

        throw new IllegalArgumentException("Edges cannot be handled with this generator.");
    }
}
