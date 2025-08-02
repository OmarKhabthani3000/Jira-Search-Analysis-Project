package paxapps.importdata.model.converters;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Event Status values as enum
 *
 * LinkTypeConverter converts DB values to/from enum
 *
 */
public enum LinkTypeEnum {

  // Note that items with canSelect set to true must precede items with false
  // values

  TEXT ("Text", "Just text, not really a link", true),
  IMAGE ("Image", "Image to display", true),
  DOCUMENT ("Document", "Document", true),
  SITE ("Site", "Official website", true),
  SOCIAL ("Social", "Social media site",
      true),
  WEB ("Web", "Other web site",
      true),
  NONE ("None", "No associated link. Used for flags, etc", true);

  private final String shortDesc;   // displayed value, also in database
  private final String longDesc;    // description in database
  private final boolean canSelect;  // true => can set using text
                                    // false => set by code only

  private static final Logger logger = LogManager.getLogger(
      LinkTypeEnum.class);

  LinkTypeEnum(String shortDesc, String longDesc, boolean canSelect) {
    this.shortDesc = shortDesc;
    this.longDesc = longDesc;
    this.canSelect = canSelect;
  }

  private static List<String> selectList =
      Stream.of(LinkTypeEnum.values())
          .filter(e -> e.canSelect)
          .map(e -> e.getShortDesc())
          .collect(Collectors.toList());

  public String getShortDesc() {
    return shortDesc;
  }

  public String getLongDesc() {
    return longDesc;
  }

  /**
   * Get enum corresponding to shortDesc, the database value
   *
   * @param desc value to look for
   * @return UserActionEnum with value
   */
  static public LinkTypeEnum getEnumfromShortDesc(String desc) {
    for (LinkTypeEnum e : LinkTypeEnum.values()) {
      if (e.shortDesc.equals(desc)) {
        return e;
      }
    }
    throw new IllegalArgumentException(desc);
  }

  /**
   * Get selectable values to use as select list
   *
   * @return
   */
  static public List<String> getSelectList() {
    return selectList;
  }
}
