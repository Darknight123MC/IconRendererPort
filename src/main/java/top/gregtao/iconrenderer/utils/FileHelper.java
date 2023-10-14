package top.gregtao.iconrenderer.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraftforge.registries.ForgeRegistries;
import top.gregtao.iconrenderer.IconRenderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static File filePath = new File("./IconRendererOutput/");
    public File file, entityFile;
    public String modId;
    public List<JsonMeta> jsonMetas = new ArrayList<>();
    public List<EntityJsonMeta> entityJsonMetas = new ArrayList<>();

    public FileHelper(String modId) throws IOException {
        this.modId = modId;
        this.file = new File(filePath.toString() + "/" + modId + ".json");
        this.entityFile = new File(filePath.toString() + "/" + modId + "_entity.json");
        if (!filePath.exists() && !filePath.mkdir()) {
            IconRenderer.logger.error("Could not mkdir " + filePath);
        } else if (!this.file.exists() && !this.file.createNewFile()) {
            IconRenderer.logger.error("Could not create new file " + this.file);
        } else if (!this.entityFile.exists() && !this.entityFile.createNewFile()) {
            IconRenderer.logger.error("Could not create new file " + this.entityFile);
        } else {
            IconRenderer.logger.info("Exporting data of " + this.modId);
            this.fromModId();
            this.readNamesByLang();
            this.writeToFile();
            IconRenderer.logger.info("Exported data of " + this.modId);
        }
    }

    public void fromModId() {
        /*
        for (ItemGroup group : ItemGroup.GROUPS) {
            if (group != ItemGroup.HOTBAR && group != ItemGroup.INVENTORY && group != ItemGroup.SEARCH) {
                DefaultedList<ItemStack> itemStacks = DefaultedList.of();
                group.appendStacks(itemStacks);
                for (ItemStack itemStack : itemStacks) {
                    if (Registry.ITEM.getId(itemStack.getItem()).getNamespace().equals(this.modId)) {
                        this.jsonMetas.add(new JsonMeta(itemStack, group));
                    }
                    // Debug
                    if (itemStack.isOf(Items.DIAMOND_AXE)) {
                        this.jsonMetas.add(new JsonMeta(itemStack, group));
                        break;
                    }

                }
            }
        }
        */

        ForgeRegistries.ITEMS.getEntries().iterator().forEachRemaining(e -> {
            if (e.getKey().getRegistryName().getNamespace().equals(modId)) {
                this.jsonMetas.add(new JsonMeta(e));
            }
        });
        Registry.ENTITY_TYPE.forEach(this::putEntity);
    }

    public void putEntity(EntityType<? extends Entity> type) {
        if (!type.getLootTableId().getNamespace().equals(this.modId)) return;
        Entity entity = type.create(MinecraftClient.getInstance().world);
        if (!(entity instanceof MobEntity)) return;
        this.entityJsonMetas.add(new EntityJsonMeta(entity));
    }

    public void readNamesByLang() {
        resetLanguage("en_us");
        for (JsonMeta meta : this.jsonMetas) {
            meta.enName = meta.itemStack.getName().getString();
        }
        for (EntityJsonMeta meta : this.entityJsonMetas) {
            meta.enName = meta.entity.getDisplayName().getString();
            meta.mod = meta.entity.getType().getLootTableId().getNamespace();
        }
        resetLanguage("zh_cn");
        for (JsonMeta meta : this.jsonMetas) {
            meta.zhName = meta.itemStack.getName().getString();
            //meta.creativeTab = meta.itemGroup.getDisplayName().getString();
        }
        for (EntityJsonMeta meta : this.entityJsonMetas) {
            meta.zhName = meta.entity.getDisplayName().getString();
        }
    }

    public void writeToFile() {
        try (final FileWriter m_writer = new FileWriter(this.file, StandardCharsets.UTF_8)) {
            for (JsonMeta meta : this.jsonMetas) {
                try {
                    m_writer.write(meta.toJsonObject().toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (final FileWriter e_writer = new FileWriter(this.entityFile, StandardCharsets.UTF_8);) {
            for (EntityJsonMeta meta : this.entityJsonMetas) {
                try {
                    e_writer.write(meta.toJsonObject().toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void resetLanguage(String lang) {
        MinecraftClient client = MinecraftClient.getInstance();
        System.out.println(lang);
        if (!client.options.language.equals(lang)) {
            LanguageDefinition langDefinition = new LanguageDefinition(lang, "", "", false);
            client.getLanguageManager().setLanguage(langDefinition);
            client.options.language = langDefinition.getCode();
            client.reloadResources();
            client.options.write();
            client.getLanguageManager().reload(client.getResourceManager());
        }
    }

}
