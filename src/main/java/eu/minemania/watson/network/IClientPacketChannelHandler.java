package eu.minemania.watson.network;

public interface IClientPacketChannelHandler
{
    void registerClientChannelHandler(IPluginChannelHandlerExtended handler);

    void unregisterClientChannelHandler(IPluginChannelHandlerExtended handler);
}
