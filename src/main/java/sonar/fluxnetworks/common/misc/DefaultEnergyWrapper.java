package sonar.fluxnetworks.common.misc;

import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.api.network.ITransferHandler;

/**
 * Uses forge's own energy wrapper and also IFNEnergyStorage
 */
public class DefaultEnergyWrapper implements IEnergyStorage, IFNEnergyStorage {

    private final IFluxDevice device;
    private final Direction side;

    public DefaultEnergyWrapper(IFluxDevice device, Direction side) {
        this.device = device;
        this.side = side;
    }

    ///// FLUX EXTENDED \\\\\

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        ITransferHandler handler = device.getTransferHandler();
        if (device.isActive()) {
            return handler.receiveFromAdjacency(maxReceive, side, simulate);
        }
        return 0;
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergyStoredL() {
        return device.getDeviceType().isPoint() ? Long.MAX_VALUE : 0;
    }

    @Override
    public long getMaxEnergyStoredL() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean canExtractL() {
        return false;
    }

    @Override
    public boolean canReceiveL() {
        return device.getDeviceType().isPlug();
    }

    ///// FORGE \\\\\

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        ITransferHandler handler = device.getTransferHandler();
        if (device.isActive()) {
            return (int) handler.receiveFromAdjacency(maxReceive, side, simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return device.getDeviceType().isPoint() ? Integer.MAX_VALUE : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return device.getDeviceType().isPlug();
    }
}