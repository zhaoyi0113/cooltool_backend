package com.cooltoo.go2nurse.beans;

import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.util.VerifyUtil;

import java.util.*;

/**
 * Created by yzzhao on 6/22/16.
 */
public class CoursesGroupBean {

    private long id;
    private String type;
    private String description;
    private String introduction;
    private String imageUrl;
    private String name;
    private Object courses = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCourseSize() {
        if (courses instanceof List) {
            return ((List) courses).size();
        }
        return 0;
    }

    public Object getCourses() {
        return courses;
    }

    public void setCourses(Object courses) {
        this.courses = courses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static List<CoursesGroupBean> parseObjectToBean(Object objMap, boolean diagnosticCourses) {
        List<CoursesGroupBean> retVal = new ArrayList<>();
        if (!(objMap instanceof Map)) {
            return retVal;
        }

        List<DiagnosticEnumeration> remainedDiagnostic = null;
        if (diagnosticCourses) {
            remainedDiagnostic = DiagnosticEnumeration.getAllDiagnostic();
        }

        CoursesGroupBean bean = null;
        Map map = (Map)objMap;
        Set keys = map.keySet();
        for (Object obj : keys) {
            bean = new CoursesGroupBean();
            if (obj instanceof CourseCategoryBean) {
                CourseCategoryBean key = (CourseCategoryBean)obj;
                Object value = map.get(key);

                String name = key.getName();
                if (CourseRelationManageService.category_all.equals(key.getName())) {
                    name = DiagnosticEnumeration.EXTENSION_NURSING.name();
                }
                bean.setId(key.getId());
                bean.setType(name);
                bean.setName(name);
                bean.setDescription(key.getIntroduction());
                bean.setImageUrl(key.getImageUrl());
                bean.setCourses(value);
            }
            else if (obj instanceof DiagnosticEnumeration) {
                DiagnosticEnumeration key = (DiagnosticEnumeration)obj;
                Object value = map.get(key);

                if (null == value) {
                    value = new ArrayList<>();
                }

                remainedDiagnostic.remove(key);

                bean.setId(key.ordinal());
                bean.setType(key.name());
                bean.setName(key.name());
                bean.setDescription("");
                bean.setIntroduction("");
                bean.setImageUrl("");
                bean.setCourses(value);
            }
            retVal.add(bean);
        }

        if (null!=remainedDiagnostic && !remainedDiagnostic.isEmpty()) {
            for (DiagnosticEnumeration key : remainedDiagnostic) {
                bean = new CoursesGroupBean();
                bean.setId(key.ordinal());
                bean.setType(key.name());
                bean.setName(key.name());
                bean.setDescription("");
                bean.setIntroduction("");
                bean.setImageUrl("");
                bean.setCourses(new ArrayList<>());
                retVal.add(bean);
            }
        }

        return retVal;
    }

    public static boolean isCoursesGroupHasCourses(List<CoursesGroupBean> group) {
        if (VerifyUtil.isListEmpty(group)) {
            return false;
        }
        for (CoursesGroupBean tmp : group) {
            if (tmp.getCourseSize()>0) {
                return true;
            }
        }
        return false;
    }

    public static void sortCourseArrays(List<CoursesGroupBean> beans) {
        Collections.sort(beans, new Comparator<CoursesGroupBean>() {
            @Override
            public int compare(CoursesGroupBean o1, CoursesGroupBean o2) {
                if (null != o1 && null != o2) {
                    long delta = (o1.getId() - o2.getId());
                    if (o1.getId() < 0 && o2.getId() < 0) {
                        return delta > 0 ? 1 : (delta < 0 ? -1 : 0);
                    } else {
                        return delta > 0 ? -1 : (delta < 0 ? 1 : 0);
                    }
                }
                if (o1 == null || o1.getId() < 0) {
                    return 1;
                }
                if (o2 == null || o2.getId() < 0) {
                    return -1;
                }
                return 0;
            }
        });
    }
}
