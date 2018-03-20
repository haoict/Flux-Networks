package sonar.flux.common.tileentity;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.api.IFlexibleGui;
import sonar.core.network.sync.SyncEnum;
import sonar.flux.api.energy.ITransferHandler;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.client.GuiTab;
import sonar.flux.common.containers.ContainerFlux;
import sonar.flux.connection.transfer.ControllerTransfer;
import sonar.flux.connection.transfer.handlers.SingleTransferHandler;

public class TileController extends TileFlux implements IFlexibleGui, IFluxController {
	public SyncEnum<PriorityMode> sendMode = new SyncEnum(PriorityMode.values(), 10);
	public SyncEnum<PriorityMode> receiveMode = new SyncEnum(PriorityMode.values(), 11);
	public SyncEnum<TransmitterMode> transmitter = new SyncEnum(TransmitterMode.values(), 12);
	public SyncEnum<TransferMode> transfer = new SyncEnum(TransferMode.values(), 13);
	public final SingleTransferHandler handler = new SingleTransferHandler(this, new ControllerTransfer(this));

	public TileController() {
		super(ConnectionType.CONTROLLER);
		syncList.addParts(sendMode, receiveMode, transmitter, transfer);
		customName.setDefault("Flux Controller");
	}

	@Override
	public PriorityMode getSendMode() {
		return sendMode.getObject();
	}

	@Override
	public PriorityMode getReceiveMode() {
		return receiveMode.getObject();
	}

	@Override
	public TransmitterMode getTransmitterMode() {
		return transmitter.getObject();
	}

	@Override
	public TransferMode getTransferMode() {
		return transfer.getObject();
	}

	public boolean hasTransfers() {
		return true;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return false;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		switch (id) {
		case 10:
			sendMode.writeToBuf(buf);
			break;
		case 11:
			receiveMode.writeToBuf(buf);
			break;
		case 12:
			transfer.writeToBuf(buf);
			break;
		case 13:
			transmitter.writeToBuf(buf);
			break;
		case 14:
			customName.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		switch (id) {
		case 10:
			sendMode.readFromBuf(buf);
			network.markTypeDirty(FluxCache.controller);
			break;
		case 11:
			receiveMode.readFromBuf(buf);
			network.markTypeDirty(FluxCache.controller);
			break;
		case 12:
			transfer.readFromBuf(buf);
			break;
		case 13:
			transmitter.readFromBuf(buf);
			break;
		case 14:
			customName.readFromBuf(buf);
			break;
		}
	}

	@Override
	public ITransferHandler getTransferHandler() {
		return handler;
	}
}