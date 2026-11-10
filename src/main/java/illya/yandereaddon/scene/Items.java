package illya.yandereaddon.scene;
public record Items(String mainHand, String offHand, String head, String helmet,
    String chestplate, String leggings, String boots,
    ItemPose mainPose, ItemPose offPose, ItemPose headPose) {}