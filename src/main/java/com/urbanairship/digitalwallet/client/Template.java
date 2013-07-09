package com.urbanairship.digitalwallet.client;


import com.google.common.base.Preconditions;
import com.urbanairship.digitalwallet.client.exception.InvalidParameterException;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template extends PassToolsClient {

    private Map<String, Object> templateHeader; /* keys are id,name,description... */
    private Map<String, JSONObject> fieldsModel; /* field key + field values = {value, label, changeMessage..} */
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private String type;
    private String projectType;
    private Date updatedAt;
    private Date createdAt;
    private String vendor;
    private Long vendorId;

    private boolean deleted;
    private boolean disabled;

    /*
     * Method      Path                            Description
     **  GET         /headers                        list out the headers of all templates for this user.
     **  GET         /{templateId}                   Get a template based on its id
     **  GET         /id/{externalId}                Get a template based on its external id
     ** DELETE      /{templateId}                   Delete a template based on its template id
     ** DELETE      /id/{externalId}                Delete a template based on its external id
     *
     ** POST        /                               Create a new template
     ** POST        /id/{externalId}                Create a new template and assign it an external id
     ** POST        /{projectId}                    Create a template and assign it to a project
     ** POST        /{projectId}/id/{externalId}    Create a template and assign it to a project, and give the template an external id
     * POST        /duplicate/{templateId}         Create a new template with the contents of the specified template.
     * POST        /duplicate/id/{externalId}      Create a new template with the contents of the specified template, by external id.
     * PUT         /{strTemplateId}                Modify the specified template
     * PUT         /id/{externalId}                Modify the specified template
     *
     * POST        /{templateId}/locations         Add locations to a template
     * POST        /id/{externalId}/locations      Add locations to a template based on the templates external id
     * DELETE      /{templateId}/location/{locationId} Delete a location from a template
     * DELETE      /id/{externalId}/location/{locationId} Delete a location from a template based on external id
     */

    public Template() {

    }

    public Template(JSONObject o) {
        assign(o);
    }

    private static void checkNotNull(Long templateId) {
        if (templateId == null) {
            throw new InvalidParameterException("please pass a valid template Id in!");
        }
    }

    /**
     * Create a template with the provided fields
     *
     * @param name         Name of the template
     * @param description  Description of the template
     * @param templateType Template Type, should be an enum instead of a string.                           TODO convert to an enum.
     * @param headers      Map of header values, JSON Object including value, fieldType and formatType     TODO, get rid of fieldType and formatType
     * @param fields       Map of fields for the template. JSON Object of the fields.
     * @return Template ID of the newly created template.
     * @throws InvalidParameterException if missing a required parameter
     *                                   RuntimeException with other errors.
     */
    public static Long createTemplate(String name, String description, String templateType, Map<String, Object> headers, Map<String, Object> fields) {
        return createTemplateInternal(name, description, templateType, headers, fields, null, null);
    }

    public static Long createTemplate(Long projectId, String name, String description, String templateType, Map<String, Object> headers, Map<String, Object> fields) {
        return createTemplateInternal(name, description, templateType, headers, fields, projectId, null);
    }

    public static Long createTemplate(String externalId, String name, String description, String templateType, Map<String, Object> headers, Map<String, Object> fields) {
        return createTemplateInternal(name, description, templateType, headers, fields, null, externalId);
    }

    public static Long createTemplate(Long projectId, String externalId, String name, String description, String templateType, Map<String, Object> headers, Map<String, Object> fields) {
        return createTemplateInternal(name, description, templateType, headers, fields, projectId, externalId);
    }

    private static Long createTemplateInternal(String name, String description, String templateType, Map<String, Object> headers, Map<String, Object> fields, Long projectId, String externalId) {
        try {
            /* check preconditions */
            try {
                Preconditions.checkNotNull(fields, "please pass a map of fields in!");
                Preconditions.checkNotNull(headers, "please pass a map of headers in!");
                Preconditions.checkNotNull(name, "please pass a template name in!");
                Preconditions.checkNotNull(description, "please pass a template description in!");
                Preconditions.checkNotNull(templateType, "please pass a template type in!");
            } catch (NullPointerException e) {
                /* thrown by preconditions checks */
                throw new InvalidParameterException(e.getMessage());
            }

            StringBuilder builder = new StringBuilder(PassTools.API_BASE);
            builder.append("/template");

            if (projectId != null) {
                builder.append('/').append(projectId);
            }

            if (externalId != null) {
                builder.append("/id/").append(URLEncoder.encode(externalId, "UTF-8"));
            }

            JSONObject jsonFields = new JSONObject(fields);
            JSONObject jsonHeaders = new JSONObject(headers);

            Map<String, Object> formFields = new HashMap<String, Object>();
            Map<String, Object> json = new HashMap<String, Object>();

            json.put("fields", jsonFields);
            json.put("headers", jsonHeaders);
            json.put("name", name);
            json.put("description", description);
            json.put("type", templateType);
            formFields.put("json", new JSONObject(json));

            PassToolsResponse response = post(builder.toString(), formFields);
            JSONObject jsonObj = response.getBodyAsJSONObject();
            Object o = jsonObj.get("templateId");

            if (o != null) {
                Long id = null;
                if (o instanceof Long) {
                    id = (Long) o;
                } else {
                    try {
                        id = Long.valueOf(o.toString());
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
                return id;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Template getTemplate(Long templateId) {
        try {
            checkNotNull(templateId);

            String url = PassTools.API_BASE + "/template/" + templateId.toString();
            PassToolsResponse response = get(url);

            return new Template(response.getBodyAsJSONObject());
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Template getTemplate(String externalId) {
        try {
            String url = PassTools.API_BASE + "/template/id/" + externalId;
            PassToolsResponse response = get(url);
            return new Template(response.getBodyAsJSONObject());
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void delete(Long templateId) {
        try {
            checkNotNull(templateId);
            String url = PassTools.API_BASE + "/template/" + templateId.toString();
            PassToolsResponse response = delete(url);
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteX(String externalId) {
        try {
            String url = PassTools.API_BASE + "/template/id/" + externalId;
            PassToolsResponse response = delete(url);
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<JSONObject> getMyTemplateHeaders() {
        try {

            String url = PassTools.API_BASE + "/template/headers";
            PassToolsResponse response = get(url);

            JSONObject jsonResponse = response.getBodyAsJSONObject();
            JSONArray templatesArray = (JSONArray) jsonResponse.get("templateHeaders");

            return (List<JSONObject>) templatesArray;

        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getTemplateHeader() {
        return templateHeader;
    }

    public Map<String, JSONObject> getFieldsModel() {
        return fieldsModel;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    private void assign(JSONObject response) {
        reset();


        JSONObject headers = (JSONObject) response.get("templateHeader");
        this.templateHeader = new JSONObject();
        this.fieldsModel = (JSONObject) response.get("fieldsModel");

        for (Map.Entry<String, Object> current : ((Map<String, Object>) headers).entrySet()) {
            String key = current.getKey();
            Object value = current.getValue();
            if (key.equals("id")) {
                this.id = toLong(value);
            } else if (key.equals("name")) {
                this.name = value.toString();
            } else if (key.equals("description")) {
                this.description = value.toString();
            } else if (key.equals("projectType")) {
                this.projectType = value.toString();
            } else if (key.equals("type")) {
                this.type = value.toString();
            } else if (key.equals("updatedAt")) {
                this.updatedAt = toTime(value.toString());
            } else if (key.equals("createdAt")) {
                this.createdAt = toTime(value.toString());
            } else if (key.equals("disabled")) {
                this.disabled = toBool(value);
            } else if (key.equals("deleted")) {
                this.deleted = toBool(value);
            } else if (key.equals("projectId")) {
                this.projectId = toLong(value);
            } else if (key.equals("vendor")) {
                this.vendor = value.toString();
            } else if (key.equals("vendorId")) {
                this.vendorId = toLong(value);
            } else {
                this.templateHeader.put(key, current.getValue());
            }
        }
    }

    private Date toTime(String time) {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
        org.joda.time.DateTime dt = fmt.parseDateTime(time);
        if (dt != null) {
            return dt.toDate();
        }
        return null;
    }

    private Boolean toBool(Object o) {
        Boolean b = false;
        if (o != null) {
            if (o instanceof Boolean) {
                b = (Boolean) o;
            } else if (o instanceof Long) {
                b = (0 != (Long) o);
            } else if (o instanceof Integer) {
                b = (0 != (Integer) o);
            } else if (o instanceof Double) {
                b = (0. != (Double) o);
            } else if (o instanceof String) {
                String str = (String) o;
                b = str.equalsIgnoreCase("true") || str.equals("1");
            }
        }
        return b;
    }

    private Long toLong(Object o) {
        Long l = null;

        if (o != null) {
            if (o instanceof Long) {
                l = (Long) o;
            } else {
                try {
                    l = Long.valueOf(o.toString());
                } catch (NumberFormatException e) {
                    l = null;
                }
            }
        }

        return l;
    }

    private void reset() {
        this.id = null;
        this.description = null;
        this.type = null;
        this.name = null;
        this.templateHeader = null;
        this.fieldsModel = null;
        this.projectId = null;
        this.projectType = null;
        this.updatedAt = null;
        this.createdAt = null;
        this.vendor = null;
        this.vendorId = null;
    }
}
