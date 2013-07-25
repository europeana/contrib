package eu.europeana.sip.licensing.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CreativeCommonsModel result from Creative Commons License api.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
@XStreamAlias("licenseclass")
public class CreativeCommonsModel {

    @XStreamAlias("label")
    private String label;

    @XStreamImplicit(itemFieldName = "field")
    private List<Field> field;

    public static class Field {

        @XStreamAsAttribute
        private String id;

        private String label;

        private String description;

        private String type;

        public String getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }

        public String getType() {
            return type;
        }

        public List<Enum> getEnum() {
            return $enum;
        }

        @Override
        public String toString() {
            return "Field{" +
                    "id='" + id + '\'' +
                    ", label='" + label + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", _enum=" + $enum +
                    '}';
        }

        @XStreamImplicit(itemFieldName = "enum")
        private List<Enum> $enum;

        public static class Enum {

            @XStreamAsAttribute
            private String id;

            private String label;

            private String description;

            public String getId() {
                return id;
            }

            public String getLabel() {
                return label;
            }

            public String getDescription() {
                return description;
            }

            @Override
            public String toString() {
                return "Enum{" +
                        "id='" + id + '\'' +
                        ", label='" + label + '\'' +
                        ", description='" + description + '\'' +
                        '}';
            }
        }

    }

    public String getLabel() {
        return label;
    }

    public List<Field> getField() {
        return field;
    }

    public Field findFieldById(String fieldId) {
        for (CreativeCommonsModel.Field field : getField()) {
            if (field.getId().equals(fieldId)) {
                return field;
            }
        }
        return null;
    }

    public Map<String, String> getJurisdictions() {
        Map<String, String> jursidictions = new HashMap<String, String>();
        Field fields = findFieldById("jurisdiction");
        for (Field.Enum $enum : fields.getEnum()) {
            jursidictions.put($enum.getId(), $enum.getLabel());
        }
        return jursidictions;
    }

    @Override
    public String toString() {
        return "CreativeCommonsModel{" +
                "label='" + label + '\'' +
                ", field=" + field +
                '}';
    }
}
