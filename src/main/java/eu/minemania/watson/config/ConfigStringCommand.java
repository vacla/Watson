package eu.minemania.watson.config;

import com.google.gson.JsonElement;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.options.ConfigString;

public class ConfigStringCommand extends ConfigString{
	private String oldValue;
	
	public ConfigStringCommand(String name, String defaultValue, String comment) {
		super(name, defaultValue, comment);
	}

	@Override
	public void setValueFromString(String value)
    {
        this.oldValue = getStringValue();
        super.setValueFromString(value);
    }
	
	public String getOldValue() {
		return this.oldValue;
	}
	
	@Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                super.setValueFromString(element.getAsString());
                this.oldValue = getStringValue();
            }
            else
            {
                MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }
}
