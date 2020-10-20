package sonar.fluxnetworks.common.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

import javax.annotation.Nonnull;

/**
 * Server and client are same class
 */
public class ContainerConnector<T extends INetworkConnector> extends Container {

    public T connector;

    public ContainerConnector(int windowId, @Nonnull PlayerInventory inv, @Nonnull T connector) {
        super(RegistryBlocks.CONTAINER_CONNECTOR, windowId);
        this.connector = connector;
        connector.onContainerOpened(inv.player);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        if (connector instanceof IFluxDevice) {
            return ((IFluxDevice) connector).getFluxWorld() == playerIn.getEntityWorld();
        }
        return true;
    }

    @Override
    public void onContainerClosed(@Nonnull PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        connector.onContainerClosed(playerIn);
    }
}