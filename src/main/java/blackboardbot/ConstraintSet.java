package blackboardbot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashSet;

public class ConstraintSet implements Constraints {
    public ConstraintSet(String term, String session) {
        this.term = term;
        this.session = session;
    }

    @NotNull public String term;
    @NotNull public String session;

    @Nullable HashSet<Constraint> constraints;

    enum ConstraintType {INCLUDE, EXCLUDE}

    class Constraint {
        Constraint(@NotNull ConstraintType type) {
            this.type = type;
        }

        @NotNull ConstraintType type;

        @Nullable HashSet<String> courseId;
        @Nullable HashSet<String> department;
        @Nullable HashSet<Integer> course;
        @Nullable Integer courseGreaterThan;
        @Nullable Integer courseLessThan;
        @Nullable HashSet<String> instructorId;

        private JSONArray setToJson(HashSet<String> set) {
            JSONArray arr = new JSONArray();

            for (String s : set) {
                arr.add(s);
            }

            return arr;
        }

        JSONObject toJson() {
            JSONObject obj = new JSONObject();

            obj.put("type", type.toString().toLowerCase());

            if (courseId != null) {
                obj.put("courseId", setToJson(courseId));
            }

            if (department != null) {
                obj.put("department", setToJson(department));
            }

            if (course != null) {
                obj.put("course", course);
            }

            if (courseGreaterThan != null) {
                obj.put("courseGreaterThan", courseGreaterThan);
            }

            if (courseLessThan != null) {
                obj.put("courseLessThan", courseLessThan);
            }

            if (instructorId != null) {
                obj.put("instructorId", setToJson(instructorId));
            }

            return obj;
        }

        @Override
        public String toString(){
            return toJson().toJSONString();
        }
    }

    @Override
    public void addConstraint(String type, String description) {
        if (constraints == null) {
            constraints = new HashSet<>();
        }

        Constraint constraint;

        if (type.toLowerCase().contains("include")) {
            constraint = new Constraint(ConstraintType.INCLUDE);
        } else if (type.toLowerCase().contains("exclude")) {
            constraint = new Constraint(ConstraintType.EXCLUDE);
        } else {
            System.out.println("\naddConstraint received a bad type identifier\n");
            constraint = new Constraint(ConstraintType.INCLUDE);
        }

        JSONParser parser = new JSONParser();
        JSONObject obj;
        JSONArray arr;
        Long i;

        try {
            obj = (JSONObject) parser.parse(description);

            if (obj == null) {
                return;
            }

            arr = (JSONArray) obj.get("courseId");
            if (arr != null) {
                if (constraint.courseId == null) constraint.courseId = new HashSet<>();
                for (Object s : arr) {
                    constraint.courseId.add((String) s);
                }
            }

            arr = (JSONArray) obj.get("department");
            if (arr != null) {
                if (constraint.department == null) constraint.department = new HashSet<>();
                for (Object s : arr) {
                    constraint.department.add((String) s);
                }
            }

            arr = (JSONArray) obj.get("course");
            if (arr != null) {
                if (constraint.course == null) constraint.course = new HashSet<>();
                for (Object s : arr) {
                    constraint.course.add((Integer) s);
                }
            }

            i = (Long) obj.get("courseGreaterThan");
            if (i != null) {
                constraint.courseGreaterThan = i.intValue();
            }

            i = (Long) obj.get("courseLessThan");
            if (i != null) {
                constraint.courseLessThan = i.intValue();
            }

            arr = (JSONArray) obj.get("instructorId");
            if (arr != null) {
                if (constraint.instructorId == null) constraint.instructorId = new HashSet<>();
                for (Object s : arr) {
                    constraint.instructorId.add((String) s);
                }
            }

        }catch(ParseException pe){
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
            return;
        }

        constraints.add(constraint);
    }

    @Override
    public String toString(){
        JSONObject obj = new JSONObject();

        obj.put("term", term);
        obj.put("session", session);

        JSONArray constraintObjects = new JSONArray();

        for (Constraint constraint : constraints) {
            constraintObjects.add(constraint.toJson());
        }

        obj.put("constraints", constraintObjects);

        return obj.toJSONString();
    }
}
