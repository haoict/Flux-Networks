package sonar.fluxnetworks.api.energy;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.misc.FluxCapabilities;

import javax.annotation.Nonnull;

/**
 * Functions the same as {@link EnergyStorage}  but allows Long.MAX_VALUE, also uses Forge's own capability.
 * use the cap in {@link FluxCapabilities} to add support to your block to
 */
public class FNEnergyStorage implements IFNEnergyStorage, IEnergyStorage {

    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;

    public FNEnergyStorage(long capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public FNEnergyStorage(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public FNEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public FNEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        if (!canReceiveL()) {
            return 0;
        }

        long energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        if (!canExtractL()) {
            return 0;
        }

        long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public long getEnergyStoredL() {
        return energy;
    }

    @Override
    public long getMaxEnergyStoredL() {
        return capacity;
    }

    @Override
    public boolean canExtractL() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceiveL() {
        return this.maxReceive > 0;
    }


    ///// FORGE ENERGY IMPLEMENTATION \\\\\

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) Math.min(receiveEnergyL(maxReceive, simulate), Integer.MAX_VALUE);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) Math.min(extractEnergyL(maxExtract, simulate), Integer.MAX_VALUE);
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(getEnergyStoredL(), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(getMaxEnergyStoredL(), Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
        return canExtractL();
    }

    @Override
    public boolean canReceive() {
        return canReceiveL();
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IFNEnergyStorage.class, new CapStorage(), () -> new FNEnergyStorage(10000));
    }

    private static class CapStorage implements Capability.IStorage<IFNEnergyStorage> {

        @Override
        public INBT writeNBT(Capability<IFNEnergyStorage> capability, @Nonnull IFNEnergyStorage instance, Direction side) {
            return LongNBT.valueOf(instance.getEnergyStoredL());
        }

        @Override
        public void readNBT(Capability<IFNEnergyStorage> capability, IFNEnergyStorage instance, Direction side, INBT nbt) {
            if (!(instance instanceof FNEnergyStorage))
                throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
            ((FNEnergyStorage) instance).energy = ((LongNBT) nbt).getLong();
        }
    }
}