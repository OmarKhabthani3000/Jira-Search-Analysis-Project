package paxapps.importdata.model.converters;

import javafx.util.StringConverter;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Convert between database event_status_type values and enum values
 *
 * See LinkTypeEnum for details on supported values
 *
 * Use:
 * @Convert(converter = LinkTypeConverter.class)
 * and convert Strings to LinkTypeEnum
 */
@Converter(autoApply = true)
public class LinkTypeConverter extends StringConverter<LinkTypeEnum>
    implements AttributeConverter<LinkTypeEnum, String> {

  private static final Logger logger = LogManager.getLogger(
      LinkTypeConverter.class);

  @Override
  public String convertToDatabaseColumn(LinkTypeEnum ent) {
    logger.traceEntry("convertToDatabaseColumn {}", ent);
    String str = ent.getShortDesc();
    logger.traceExit("convertToDatabaseColumn {}", str);
    return str;
  }

  @Override
  public LinkTypeEnum convertToEntityAttribute(String s) {
    logger.traceEntry("convertToEntityAttribute {}", s);
    LinkTypeEnum ent = LinkTypeEnum.getEnumfromShortDesc(s);
    logger.traceExit("convertToDatabaseColumn {}", s);
    return ent;
  }

  @Override
  public String toString(LinkTypeEnum object) {
    return object.getShortDesc();
  }

  @Override
  public LinkTypeEnum fromString(String string) {
    return LinkTypeEnum.getEnumfromShortDesc(string);
  }
}
