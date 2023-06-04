package moe.seikimo.altservice.network.handler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import moe.seikimo.altservice.Configuration;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.slf4j.Logger;

@Data
@RequiredArgsConstructor
public class AbstractPacketHandler implements BedrockPacketHandler {
    protected final PlayerNetworkSession session;

    /**
     * @return The session's logger.
     */
    protected Logger getLogger() {
        return this.getSession().getLogger();
    }

    /**
     * @return The session's player.
     */
    protected Player getPlayer() {
        return this.getSession().getPlayer();
    }

    /**
     * Logs a packet to console if logging is enabled.
     * @param packet Packet that was received.
     */
    private void logPacket(BedrockPacket packet) {
        if (Configuration.get().isDebug()) {
            var name = packet.getClass().getSimpleName();
            if (!Configuration.get().getIgnoredDebugPackets().contains(name))
                this.getLogger().info("Received packet: " + name);
        }
    }

    public PacketSignal handle(AdventureSettingsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AnimatePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AnvilDamagePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AvailableEntityIdentifiersPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(BlockEntityDataPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(BlockPickRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(BookEditPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ClientCacheBlobStatusPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ClientCacheMissResponsePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ClientCacheStatusPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ClientToServerHandshakePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CommandBlockUpdatePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CommandRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CompletedUsingItemPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ContainerClosePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CraftingEventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(EducationSettingsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(EmotePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(EntityEventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(EntityFallPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(EntityPickRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(EventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(FilterTextPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(InteractPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(InventoryContentPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(InventorySlotPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(InventoryTransactionPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ItemFrameDropItemPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LabTablePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LecternUpdatePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LevelEventGenericPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LevelSoundEvent1Packet packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LevelSoundEventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LoginPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MapInfoRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MobArmorEquipmentPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MobEquipmentPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ModalFormResponsePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MoveEntityAbsolutePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MovePlayerPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MultiplayerSettingsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(NetworkStackLatencyPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PhotoTransferPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerActionPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerAuthInputPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerHotbarPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerInputPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerSkinPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PurchaseReceiptPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(RequestChunkRadiusPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ResourcePackChunkRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ResourcePackClientResponsePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(RiderJumpPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ServerSettingsRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetDefaultGameTypePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetLocalPlayerAsInitializedPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetPlayerGameTypePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SubClientLoginPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AddBehaviorTreePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AddEntityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AddHangingEntityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AddItemEntityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AddPaintingPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AddPlayerPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AvailableCommandsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(BlockEventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(BossEventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CameraPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ChangeDimensionPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ChunkRadiusUpdatedPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ClientboundMapItemDataPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CommandOutputPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ContainerOpenPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ContainerSetDataPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CraftingDataPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(DisconnectPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ExplodePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LevelChunkPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(GameRulesChangedPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(GuiDataPickItemPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(HurtArmorPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AutomationClientConnectPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LevelEventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MapCreateLockedCopyPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MobEffectPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ModalFormRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MoveEntityDeltaPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(NetworkSettingsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(NpcRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(OnScreenTextureAnimationPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerListPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlaySoundPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayStatusPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(RemoveEntityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(RemoveObjectivePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ResourcePackChunkDataPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ResourcePackDataInfoPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ResourcePacksInfoPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ResourcePackStackPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(RespawnPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ScriptCustomEventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ServerSettingsResponsePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ServerToClientHandshakePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetCommandsEnabledPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetDifficultyPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetDisplayObjectivePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetEntityDataPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetEntityLinkPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetEntityMotionPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetHealthPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetLastHurtByPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetScoreboardIdentityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetScorePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetSpawnPositionPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetTimePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SettingsCommandPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SetTitlePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ShowCreditsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ShowProfilePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ShowStoreOfferPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SimpleEventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SpawnExperienceOrbPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SpawnParticleEffectPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(StartGamePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(StopSoundPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(StructureBlockUpdatePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(StructureTemplateDataRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(StructureTemplateDataResponsePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(TakeItemEntityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(TextPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(TickSyncPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(TransferPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateAttributesPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateBlockPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateBlockPropertiesPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateBlockSyncedPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateEquipPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateSoftEnumPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateTradePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(BiomeDefinitionListPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LevelSoundEvent2Packet packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(NetworkChunkPublisherUpdatePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(VideoStreamConnectPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CodeBuilderPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(EmoteListPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ItemStackRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ItemStackResponsePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerArmorDamagePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerEnchantOptionsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CreativeContentPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdatePlayerGameTypePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PositionTrackingDBServerBroadcastPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PositionTrackingDBClientRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PacketViolationWarningPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(DebugInfoPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(MotionPredictionHintsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AnimateEntityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CameraShakePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CorrectPlayerMovePredictionPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerFogPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ItemComponentPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ClientboundDebugRendererPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SyncEntityPropertyPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AddVolumeEntityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(RemoveVolumeEntityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(NpcDialoguePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SimulationTypePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(EduUriResourcePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CreatePhotoPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateSubChunkBlocksPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SubChunkPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(SubChunkRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PhotoInfoRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(PlayerStartItemCooldownPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ScriptMessagePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CodeBuilderSourcePacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(TickingAreasLoadStatusPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(DimensionDataPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(AgentActionEventPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ChangeMobPropertyPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(LessonProgressPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(RequestAbilityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(RequestPermissionsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ToastRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateAbilitiesPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateAdventureSettingsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(DeathInfoPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(EditorNetworkPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(FeatureRegistryPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ServerStatsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(GameTestRequestPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(GameTestResultsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UpdateClientInputLocksPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(ClientCheatAbilityPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CameraPresetsPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CameraInstructionPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(UnlockedRecipesPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(CompressedBiomeDefinitionListPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(TrimDataPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }


    @Override
    public PacketSignal handle(OpenSignPacket packet) {
        logPacket(packet);
        return PacketSignal.UNHANDLED;
    }
}
