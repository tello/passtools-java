package com.urbanairship.digitalwallet.client;

/*
 *   Method      Path                            Description
 *   GET         /                               Get a list of projects for this user
 *   GET         /{projectID}                    Get a project based on its id
 *   GET         /id/{externalUID}               Get a project based on its external id
 *   POST        /                               Create a new project
 *   POST        /id/{externalID}                Create a new project with an externalID
 *   POST        /{layoutID}                     Create a project based on a layout
 *   PUT         /{projectID}                    Update a project
 *   PUT         /id/{externalUID}               Update a project based on its external id
 *   DELETE      /{projectID}                    Delete the specified project
 *   DELETE      /id/{externalID}                Delete the specified project based on its external id.
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project extends PassToolsClient {

    private Long id;
    private String name;
    private String description;
    private String projectType;
    private List<Template> templates;

    private static final String missingExternalIdError = "please pass a valid external Id in!";
    private static final String missingNameError = "please pass a valid name in!";
    private static final String missingDescriptionError = "please pass a valid description in!";
    private static final String missingProjectTypeError = "please pass a valid project type in!";

    public Project(Long projectId) {

    }

    public Project(JSONObject json) {
        assign(json);
    }

    /**
     * Get the list of projects
     *
     * @return a list of projects for this user.
     */
    public static List<Project> getProjects(int pageSize, int page) {
        try {
            List<Project> projects = new ArrayList<Project>();
            StringBuilder builder = new StringBuilder(getBaseUrl());
            builder.append("?pageSize=").append(pageSize).append("&page=").append(page);
            PassToolsResponse response = get(builder.toString());

            JSONObject jsonResponse = response.getBodyAsJSONObject();
            JSONArray projectArray = (JSONArray) jsonResponse.get("projects");

            if (projectArray != null) {
                for (Object o : projectArray.toArray()) {
                    if (o instanceof JSONObject) {
                        projects.add(new Project((JSONObject) o));
                    }
                }
            }

            return projects;
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the specified project.
     *
     * @param id    ID of the project you want to get.
     * @return      the resulting project.
     */
    public static Project getProject(long id) {
        try {
            String url = getBaseUrl() + "/" + id;
            PassToolsResponse response = get(url);

            JSONObject jsonResponse = response.getBodyAsJSONObject();
            return new Project(jsonResponse);
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the specified project, by external id.
     *
     * @param externalId    ID of the project you want to get.
     * @return      the resulting project.
     */
    public static Project getProject(String externalId) {
        try {
            checkNotNull(externalId, missingExternalIdError);
            String url = getBaseUrl() + "/id/" + externalId;
            PassToolsResponse response = get(url);

            JSONObject jsonResponse = response.getBodyAsJSONObject();
            return new Project(jsonResponse);
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a project with the specified, name, description and type.
     *
     * @param name          Name of newly created project.
     * @param description   Description of the newly created project.
     * @param projectType   Type of project you're creating.
     * @return              The resulting project.
     */
    public static Project createProject(String name, String description, String projectType) {
        return createProjectInternal(name, description, projectType, null, null);
    }

    /**
     * Create a project with the specified, name, description and type.
     *
     * @param externalId    ExternalID you want assigned to the newly created project.
     * @param name          Name of newly created project.
     * @param description   Description of the newly created project.
     * @param projectType   Type of project you're creating.
     * @return              The resulting project.
     */
    public static Project createProject(String externalId, String name, String description, String projectType) {
        checkNotNull(externalId, missingExternalIdError);
        return createProjectInternal(name, description, projectType, externalId, null);
    }

    /**
     * Create a project with the specified, name, description and type.
     *
     * @param layoutId      Layout you want to base the new project on.
     * @param name          Name of newly created project.
     * @param description   Description of the newly created project.
     * @param projectType   Type of project you're creating.
     * @return              The resulting project.
     */
    public static Project createProject(long layoutId, String name, String description, String projectType) {
        return createProjectInternal(name, description, projectType, null, layoutId);
    }


    /**
     * update the specified project.
     * @param projectId     ID of the project you want to update.
     * @param name          New name of the project.
     * @param description   New description of the project.
     * @return              The resulting project.
     */
    public static Project updateProject(long projectId, String name, String description) {
        return updateProjectInternal(projectId, name, description);
    }

    /**
     * update the specified project.
     *
     * @param externalId    ID of the project you want to update.
     * @param name          New name of the project.
     * @param description   New description of the project.
     * @return              The resulting project.
     */
    public static Project updateProject(String externalId, String name, String description) {
        checkNotNull(externalId, missingExternalIdError);
        Project project = getProject(externalId);
        if (project != null) {
            return updateProjectInternal(project.getId(), name, description);
        }
        return null;
    }

    /**
     * Delete the specified project.
     *
     * @param id    ID of the project you want to delete.
     */
    public static void deleteProject(long id) {
        try {
            String url = getBaseUrl() + "/" + id;
            PassToolsResponse response = delete(url);
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete the specified project.
     *
     * @param externalId    External ID of the project you want to delete.
     */
    public static void deleteProject(String externalId) {
        try {
            checkNotNull(externalId, missingExternalIdError);
            String url = getBaseUrl() + "/id/" + externalId;
            PassToolsResponse response = delete(url);
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getProjectType() {
        return projectType;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    private void assign(JSONObject json) {
        reset();

        if (json != null) {
            this.id = (Long) json.get("id");
            this.description = (String) json.get("description");
            this.name = (String) json.get("name");
            this.projectType = (String) json.get("projectType");
            Object templates = json.get("templates");
            if (templates != null && templates instanceof JSONArray) {
                this.templates = new ArrayList<Template>();
                for (Object o : ((JSONArray) templates).toArray()) {
                    JSONObject currentJson = (JSONObject)o;
                    this.templates.add(new Template(currentJson));
                }
            }
        }
    }

    private void reset() {
        id = null;
        name = null;
        description = null;
        projectType = null;
    }

    protected static String getBaseUrl() {
        return PassTools.API_BASE + "/project";
    }

    private static Project createProjectInternal(String name, String description, String projectType, String externalId, Long layoutId) {
        try {
            Long id = null;
            checkNotNull(projectType, missingProjectTypeError);
            checkNotNull(name, missingNameError);
            checkNotNull(description, missingDescriptionError);

            StringBuilder builder = new StringBuilder(getBaseUrl());
            if (layoutId != null) {
                builder.append("/").append(layoutId);
            } else if (externalId != null) {
                builder.append("/id/").append(externalId);
            }

            Map<String, Object> formFields = new HashMap<String, Object>();
            formFields.put("json", getJson(name, description, projectType));
            PassToolsResponse response = post(builder.toString(), formFields);
            JSONObject o = response.getBodyAsJSONObject();

            return new Project(o);
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static Project updateProjectInternal(long projectId, String name, String description) {
        try {
            checkNotNull(name, missingNameError);
            checkNotNull(description, missingDescriptionError);

            Map<String, Object> formFields = new HashMap<String, Object>();
            formFields.put("json", getJson(name, description, null));
            StringBuilder builder = new StringBuilder(getBaseUrl());
            builder.append("/").append(projectId);

            PassToolsResponse response = put(builder.toString(), formFields);
            JSONObject o = response.getBodyAsJSONObject();
            return new Project(o);
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static JSONObject getJson(String name, String description, String projectType) {
        JSONObject json = new JSONObject();
        if (name != null) json.put("name", name);
        if (description != null) json.put("description", description);
        if (projectType != null) json.put("projectType", projectType);
        return json;
    }
}
