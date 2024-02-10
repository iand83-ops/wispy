package fr.nicolas.wispy.game.items.registry;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.craft.CraftManager;
import fr.nicolas.wispy.game.craft.Definition;
import fr.nicolas.wispy.game.craft.Recipe;
import fr.nicolas.wispy.game.items.Item;
import fr.nicolas.wispy.game.items.ItemStructure;
import fr.nicolas.wispy.game.items.types.ToolItem;
import fr.nicolas.wispy.game.structure.StructureDefinition;
import fr.nicolas.wispy.game.structure.StructureShape;

public class ItemsRegistry {

    private final Item[] items = new Item[256];
    private final CraftManager craftManager;

    public ItemsRegistry(CraftManager craftManager) {
        this.craftManager = craftManager;

        register(101, new Item(Items.IRON_INGOT, "Iron ingot"));
        register(102, new Item(Items.COAL, "Coal"));
        register(103, new Item(Items.GOLD_INGOT, "Gold ingot"));
        register(104, new Item(Items.DIAMOND, "Diamond"));

        register(105, new Item(Items.STICK, "Stick"))
                .registerRecipe(4, new Recipe("A", "A"), new Definition('A', Blocks.OAK_PLANKS));

        register(106, new ToolItem(Items.WOODEN_PICKAXE, "Wooden pickaxe", Tools.PICKAXE, ToolsMaterial.WOOD))
                .registerRecipe(new Recipe("AAA", " B", " B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));
        register(107, new ToolItem(Items.WOODEN_AXE, "Wooden axe", Tools.AXE, ToolsMaterial.WOOD))
                .registerRecipe(new Recipe("AA", "AB", " B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK))
                .registerRecipe(new Recipe("AA", "BA", "B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));
        register(108, new ToolItem(Items.WOODEN_SHOVEL, "Wooden shovel", Tools.SHOVEL, ToolsMaterial.WOOD))
                .registerRecipe(new Recipe("A", "B", "B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));
        register(109, new ToolItem(Items.WOODEN_HOE, "Wooden hoe", Tools.HOE, ToolsMaterial.WOOD))
                .registerRecipe(new Recipe("AA", " B", " B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));
        register(110, new ToolItem(Items.WOODEN_SWORD, "Wooden sword", Tools.SWORD, ToolsMaterial.WOOD))
                .registerRecipe(new Recipe("A", "A", "B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));

        register(111, new ToolItem(Items.STONE_PICKAXE, "Stone pickaxe", Tools.PICKAXE, ToolsMaterial.STONE))
                .registerRecipe(new Recipe("AAA", " B", " B"), new Definition('A', Blocks.COBBLESTONE), new Definition('B', Items.STICK));
        register(112, new ToolItem(Items.STONE_AXE, "Stone axe", Tools.AXE, ToolsMaterial.STONE))
                .registerRecipe(new Recipe("AA", "AB", " B"), new Definition('A', Blocks.COBBLESTONE), new Definition('B', Items.STICK))
                .registerRecipe(new Recipe("AA", "BA", "B"), new Definition('A', Blocks.COBBLESTONE), new Definition('B', Items.STICK));
        register(113, new ToolItem(Items.STONE_SHOVEL, "Stone shovel", Tools.SHOVEL, ToolsMaterial.STONE))
                .registerRecipe(new Recipe("A", "B", "B"), new Definition('A', Blocks.COBBLESTONE), new Definition('B', Items.STICK));
        register(114, new ToolItem(Items.STONE_HOE, "Stone hoe", Tools.HOE, ToolsMaterial.STONE))
                .registerRecipe(new Recipe("AA", " B", " B"), new Definition('A', Blocks.COBBLESTONE), new Definition('B', Items.STICK));
        register(115, new ToolItem(Items.STONE_SWORD, "Stone sword", Tools.SWORD, ToolsMaterial.STONE))
                .registerRecipe(new Recipe("A", "A", "B"), new Definition('A', Blocks.COBBLESTONE), new Definition('B', Items.STICK));

        register(116, new ToolItem(Items.IRON_PICKAXE, "Iron pickaxe", Tools.PICKAXE, ToolsMaterial.IRON))
                .registerRecipe(new Recipe("AAA", " B", " B"), new Definition('A', Items.IRON_INGOT), new Definition('B', Items.STICK));
        register(117, new ToolItem(Items.IRON_AXE, "Iron axe", Tools.AXE, ToolsMaterial.IRON))
                .registerRecipe(new Recipe("AA", "AB", " B"), new Definition('A', Items.IRON_INGOT), new Definition('B', Items.STICK))
                .registerRecipe(new Recipe("AA", "BA", "B"), new Definition('A', Items.IRON_INGOT), new Definition('B', Items.STICK));
        register(118, new ToolItem(Items.IRON_SHOVEL, "Iron shovel", Tools.SHOVEL, ToolsMaterial.IRON))
                .registerRecipe(new Recipe("A", "B", "B"), new Definition('A', Items.IRON_INGOT), new Definition('B', Items.STICK));
        register(119, new ToolItem(Items.IRON_HOE, "Iron hoe", Tools.HOE, ToolsMaterial.IRON))
                .registerRecipe(new Recipe("AA", " B", " B"), new Definition('A', Items.IRON_INGOT), new Definition('B', Items.STICK));
        register(120, new ToolItem(Items.IRON_SWORD, "Iron sword", Tools.SWORD, ToolsMaterial.IRON))
                .registerRecipe(new Recipe("A", "A", "B"), new Definition('A', Items.IRON_INGOT), new Definition('B', Items.STICK));

        register(121, new ToolItem(Items.GOLD_PICKAXE, "Gold pickaxe", Tools.PICKAXE, ToolsMaterial.GOLD))
                .registerRecipe(new Recipe("AAA", " B", " B"), new Definition('A', Items.GOLD_INGOT), new Definition('B', Items.STICK));
        register(122, new ToolItem(Items.GOLD_AXE, "Gold axe", Tools.AXE, ToolsMaterial.GOLD))
                .registerRecipe(new Recipe("AA", "AB", " B"), new Definition('A', Items.GOLD_INGOT), new Definition('B', Items.STICK))
                .registerRecipe(new Recipe("AA", "BA", "B"), new Definition('A', Items.GOLD_INGOT), new Definition('B', Items.STICK));
        register(123, new ToolItem(Items.GOLD_SHOVEL, "Gold shovel", Tools.SHOVEL, ToolsMaterial.GOLD))
                .registerRecipe(new Recipe("A", "B", "B"), new Definition('A', Items.GOLD_INGOT), new Definition('B', Items.STICK));
        register(124, new ToolItem(Items.GOLD_HOE, "Gold hoe", Tools.HOE, ToolsMaterial.GOLD))
                .registerRecipe(new Recipe("AA", " B", " B"), new Definition('A', Items.GOLD_INGOT), new Definition('B', Items.STICK));
        register(125, new ToolItem(Items.GOLD_SWORD, "Gold sword", Tools.SWORD, ToolsMaterial.GOLD))
                .registerRecipe(new Recipe("A", "A", "B"), new Definition('A', Items.GOLD_INGOT), new Definition('B', Items.STICK));

        register(126, new ToolItem(Items.DIAMOND_PICKAXE, "Diamond pickaxe", Tools.PICKAXE, ToolsMaterial.DIAMOND))
                .registerRecipe(new Recipe("AAA", " B", " B"), new Definition('A', Items.DIAMOND), new Definition('B', Items.STICK));
        register(127, new ToolItem(Items.DIAMOND_AXE, "Diamond axe", Tools.AXE, ToolsMaterial.DIAMOND))
                .registerRecipe(new Recipe("AA", "AB", " B"), new Definition('A', Items.DIAMOND), new Definition('B', Items.STICK))
                .registerRecipe(new Recipe("AA", "BA", "B"), new Definition('A', Items.DIAMOND), new Definition('B', Items.STICK));
        register(128, new ToolItem(Items.DIAMOND_SHOVEL, "Diamond shovel", Tools.SHOVEL, ToolsMaterial.DIAMOND))
                .registerRecipe(new Recipe("A", "B", "B"), new Definition('A', Items.DIAMOND), new Definition('B', Items.STICK));
        register(129, new ToolItem(Items.DIAMOND_HOE, "Diamond hoe", Tools.HOE, ToolsMaterial.DIAMOND))
                .registerRecipe(new Recipe("AA", " B", " B"), new Definition('A', Items.DIAMOND), new Definition('B', Items.STICK));
        register(130, new ToolItem(Items.DIAMOND_SWORD, "Diamond sword", Tools.SWORD, ToolsMaterial.DIAMOND))
                .registerRecipe(new Recipe("A", "A", "B"), new Definition('A', Items.DIAMOND), new Definition('B', Items.STICK));

        register(131, new ItemStructure(Items.DOOR, "Door",
                new StructureShape("A", "B"), new StructureDefinition('A', Blocks.DOOR_TOP), new StructureDefinition('B', Blocks.DOOR_BOTTOM)))
                .registerRecipe(3, new Recipe("AA", "AA", "AA"), new Definition('A', Blocks.OAK_PLANKS));
    }

    public CraftManager register(int id, Item item) {
        if (items[id] != null) {
            throw new IllegalArgumentException("Item id " + id + " is already registered");
        }

        item.getType().setId(id);
        item.getType().setItem(item);
        items[id] = item;

        craftManager.setCurrentItem(item);
        return craftManager;
    }

    public Item getItem(int id) {
        return items[id].copy();
    }

}
