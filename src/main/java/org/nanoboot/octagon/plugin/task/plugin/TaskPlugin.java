///////////////////////////////////////////////////////////////////////////////////////////////
// Octagon Plugin Task: Task plugin for Octagon application.
// Copyright (C) 2021-2022 the original author or authors.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; version 2
// of the License only.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
///////////////////////////////////////////////////////////////////////////////////////////////

package org.nanoboot.octagon.plugin.task.plugin;

import java.util.Properties;
import org.nanoboot.octagon.plugin.api.core.Plugin;
import org.nanoboot.octagon.plugin.api.core.PluginStub;
import org.nanoboot.octagon.plugin.api.core.PluginStubImpl;
import org.nanoboot.octagon.plugin.task.persistence.impl.typehandlers.ImportanceTypeHandler;
import org.nanoboot.octagon.plugin.task.persistence.impl.typehandlers.ResolutionTypeHandler;
import org.nanoboot.octagon.plugin.task.persistence.impl.typehandlers.SizeTypeHandler;
import org.nanoboot.octagon.plugin.task.persistence.impl.typehandlers.StatusTypeHandler;
import org.nanoboot.powerframework.json.JsonObject;
import lombok.Getter;

/**
 *
 * @author <a href="mailto:robertvokac@nanoboot.org">Robert Vokac</a>
 * @since 0.1.0
 */
public class TaskPlugin implements Plugin {
    private static final String TASK = "task";
    @Getter
    private PluginStub pluginStub = new PluginStubImpl();

    @Override
    public String getGroup() {
        return TASK;
    }

    @Override
    public String getId() {
        return TASK;
    }

    @Override
    public String getVersion() {
        return "0.0.0";
    }

    @Override
    public String init(Properties propertiesConfiguration) {
        pluginStub.registerTypeHandler(ImportanceTypeHandler.class);
        pluginStub.registerTypeHandler(ResolutionTypeHandler.class);
        pluginStub.registerTypeHandler(SizeTypeHandler.class);
        pluginStub.registerTypeHandler(StatusTypeHandler.class);

        return null;
    }
}
