package com.esprit.backendsport.Services.Workout;

import com.esprit.backendsport.DTOs.Workout.ExerciseDTO;
import com.esprit.backendsport.DTOs.Workout.WorkoutRequestDTO;
import com.esprit.backendsport.DTOs.Workout.WorkoutResponseDTO;
import com.esprit.backendsport.Entities.Workout.Exercise;
import com.esprit.backendsport.Repository.Workout.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final ExerciseRepository exerciseRepository;

    // -----------------------------------------------------------------
    // Muscle group expansion map  (keys: frontend values, lowercase)
    // -----------------------------------------------------------------
    private static final Map<String, List<String>> MUSCLE_MAP = new HashMap<>();

    static {
        MUSCLE_MAP.put("back",        List.of("lats", "middle back", "lower back"));
        MUSCLE_MAP.put("upper legs",  List.of("quadriceps", "hamstrings"));
        MUSCLE_MAP.put("lower legs",  List.of("calves"));
        MUSCLE_MAP.put("forearms",    List.of("forearms"));
        MUSCLE_MAP.put("abs",         List.of("abdominals"));
        MUSCLE_MAP.put("shoulders",   List.of("shoulders"));
        MUSCLE_MAP.put("biceps",      List.of("biceps"));
        MUSCLE_MAP.put("glutes",      List.of("glutes"));
        MUSCLE_MAP.put("chest",       List.of("chest"));
        MUSCLE_MAP.put("triceps",     List.of("triceps"));
        // common aliases the frontend might send
        MUSCLE_MAP.put("legs",        List.of("quadriceps", "hamstrings", "calves", "glutes"));
        MUSCLE_MAP.put("core",        List.of("abdominals"));
        MUSCLE_MAP.put("arms",        List.of("biceps", "triceps", "forearms"));
    }

    // -----------------------------------------------------------------
    // Equipment normalization map  (frontend → DB canonical value)
    // All keys must be lowercase.
    // -----------------------------------------------------------------
    private static final Map<String, String> EQUIPMENT_NORM = new HashMap<>();

    static {
        EQUIPMENT_NORM.put("dumbbells",    "dumbbell");
        EQUIPMENT_NORM.put("dumbbell",     "dumbbell");
        EQUIPMENT_NORM.put("barbells",     "barbell");
        EQUIPMENT_NORM.put("barbell",      "barbell");
        EQUIPMENT_NORM.put("bodyweight",   "body only");
        EQUIPMENT_NORM.put("body weight",  "body only");
        EQUIPMENT_NORM.put("body-weight",  "body only");
        EQUIPMENT_NORM.put("body only",    "body only");
        EQUIPMENT_NORM.put("cables",       "cable");
        EQUIPMENT_NORM.put("cable",        "cable");
        EQUIPMENT_NORM.put("machines",     "machine");
        EQUIPMENT_NORM.put("machine",      "machine");
        EQUIPMENT_NORM.put("kettlebells",  "kettlebell");
        EQUIPMENT_NORM.put("kettlebell",   "kettlebell");
        EQUIPMENT_NORM.put("bands",        "bands");
        EQUIPMENT_NORM.put("resistance band", "bands");
        EQUIPMENT_NORM.put("ez bar",       "e-z curl bar");
        EQUIPMENT_NORM.put("ez-bar",       "e-z curl bar");
        EQUIPMENT_NORM.put("none",         "body only");
        EQUIPMENT_NORM.put("no equipment", "body only");
    }

    // -----------------------------------------------------------------
    // Difficulty normalization map  (frontend → DB level value)
    // All keys must be lowercase.
    // -----------------------------------------------------------------
    private static final Map<String, String> LEVEL_NORM = new HashMap<>();

    static {
        LEVEL_NORM.put("beginner",     "beginner");
        LEVEL_NORM.put("easy",         "beginner");
        LEVEL_NORM.put("intermediate", "intermediate");
        LEVEL_NORM.put("medium",       "intermediate");
        LEVEL_NORM.put("advanced",     "expert");
        LEVEL_NORM.put("expert",       "expert");
        LEVEL_NORM.put("hard",         "expert");
    }

    // -----------------------------------------------------------------
    // YouTube embed URLs (keyed by exercise name substring, case-insensitive)
    // Format: https://www.youtube.com/embed/{videoId}
    // -----------------------------------------------------------------
    private static final Map<String, String> VIDEO_MAP = new LinkedHashMap<>();

    static {
        VIDEO_MAP.put("Bench Press",                "https://www.youtube.com/embed/rT7DgCr-3pg");
        VIDEO_MAP.put("Incline Dumbbell Press",     "https://www.youtube.com/embed/8iPEnn-ltC8");
        VIDEO_MAP.put("Push-Up",                    "https://www.youtube.com/embed/_l3ySVKYVJ8");
        VIDEO_MAP.put("Pull-Up",                    "https://www.youtube.com/embed/eGo4IYlbE5g");
        VIDEO_MAP.put("Chin-Up",                    "https://www.youtube.com/embed/8-PFIVpwgk8");
        VIDEO_MAP.put("Barbell Row",                "https://www.youtube.com/embed/G8l_8chR5BE");
        VIDEO_MAP.put("Seated Cable Row",           "https://www.youtube.com/embed/GZbfZ033f74");
        VIDEO_MAP.put("Lat Pulldown",               "https://www.youtube.com/embed/CAwf7n6Luuc");
        VIDEO_MAP.put("Deadlift",                   "https://www.youtube.com/embed/op9kVnSso6Q");
        VIDEO_MAP.put("Squat",                      "https://www.youtube.com/embed/aclHkVaku9U");
        VIDEO_MAP.put("Leg Press",                  "https://www.youtube.com/embed/IZxyjW7MPJQ");
        VIDEO_MAP.put("Lunge",                      "https://www.youtube.com/embed/QOVaHwm-Q6U");
        VIDEO_MAP.put("Leg Curl",                   "https://www.youtube.com/embed/1Tq3QdYUuHs");
        VIDEO_MAP.put("Leg Extension",              "https://www.youtube.com/embed/YyvSfVjQeL0");
        VIDEO_MAP.put("Calf Raise",                 "https://www.youtube.com/embed/gwLzBJYoWlI");
        VIDEO_MAP.put("Overhead Press",             "https://www.youtube.com/embed/2yjwXTZQDDI");
        VIDEO_MAP.put("Lateral Raise",              "https://www.youtube.com/embed/3VcKaXpzqRo");
        VIDEO_MAP.put("Front Raise",                "https://www.youtube.com/embed/sOgCllSFqSs");
        VIDEO_MAP.put("Barbell Curl",               "https://www.youtube.com/embed/kwG2ipFRgfo");
        VIDEO_MAP.put("Dumbbell Curl",              "https://www.youtube.com/embed/ykJmrZ5v0Oo");
        VIDEO_MAP.put("Hammer Curl",                "https://www.youtube.com/embed/zC3nLlEvin4");
        VIDEO_MAP.put("Tricep Dip",                 "https://www.youtube.com/embed/0326dy_-CzM");
        VIDEO_MAP.put("Triceps Pushdown",           "https://www.youtube.com/embed/2-LAMcpzODU");
        VIDEO_MAP.put("Skull Crusher",              "https://www.youtube.com/embed/d_KZxkY_0cM");
        VIDEO_MAP.put("Plank",                      "https://www.youtube.com/embed/ASdvN_XEl_c");
        VIDEO_MAP.put("Crunch",                     "https://www.youtube.com/embed/Xyd_fa5zoEU");
        VIDEO_MAP.put("Leg Raise",                  "https://www.youtube.com/embed/l4kQd9eWclE");
        VIDEO_MAP.put("Hip Thrust",                 "https://www.youtube.com/embed/SEdqd1n0cvg");
        VIDEO_MAP.put("Glute Bridge",               "https://www.youtube.com/embed/OUgsJ8-Vi0E");
        VIDEO_MAP.put("Romanian Deadlift",          "https://www.youtube.com/embed/JCXUYuzwNrM");
    }

    // -----------------------------------------------------------------
    // Program size limits
    // -----------------------------------------------------------------
    private static final int BEGINNER_MAX     = 6;
    private static final int INTERMEDIATE_MAX = 8;
    private static final int ADVANCED_MAX     = 10;

    // -----------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------

    public WorkoutResponseDTO generatePrograms(WorkoutRequestDTO request) {
        List<String> muscles   = request.getMuscles();
        List<String> equipment = request.getEquipment();

        // Guard: no muscles selected → empty response
        if (muscles == null || muscles.isEmpty()) {
            return emptyResponse();
        }

        // 1. Normalize + expand muscle names
        List<String> expandedMuscles = expandMuscles(muscles);

        // 2. Normalize equipment values sent by the frontend
        List<String> normalizedEquipment = normalizeEquipment(equipment);

        // 3. Fetch exercises that target the requested muscles
        List<Exercise> candidates = exerciseRepository.findByPrimaryMusclesIn(expandedMuscles);

        // 4. Filter by equipment using flexible matching
        if (normalizedEquipment != null && !normalizedEquipment.isEmpty()) {
            candidates = candidates.stream()
                    .filter(e -> equipmentMatches(e.getEquipment(), normalizedEquipment))
                    .collect(Collectors.toList());
        }

        // 5. Shuffle for randomness
        Collections.shuffle(candidates);

        // 6. Split by level — always use the DB level values (beginner / intermediate / expert)
        Set<String> used = new HashSet<>();

        List<ExerciseDTO> beginner     = pickLevel(candidates, "beginner",     BEGINNER_MAX,     used);
        List<ExerciseDTO> intermediate = pickLevel(candidates, "intermediate",  INTERMEDIATE_MAX, used);
        List<ExerciseDTO> advanced     = pickLevel(candidates, "expert",        ADVANCED_MAX,     used);

        Map<String, List<ExerciseDTO>> programs = new LinkedHashMap<>();
        programs.put("beginner",     beginner);
        programs.put("intermediate", intermediate);
        programs.put("advanced",     advanced);

        return WorkoutResponseDTO.builder().programs(programs).build();
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------

    /** Trim + lowercase each muscle, then expand group names to raw DB muscle names. */
    private List<String> expandMuscles(List<String> muscles) {
        return muscles.stream()
                .filter(m -> m != null && !m.isBlank())
                .map(m -> m.trim().toLowerCase())
                .flatMap(m -> MUSCLE_MAP.getOrDefault(m, List.of(m)).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Trim + lowercase each equipment value, then map frontend aliases
     * (e.g. "Dumbbells" → "dumbbell", "Bodyweight" → "body only") to DB canonical values.
     * Unknown values are kept as-is (lowercased) so they can still fuzzy-match in equipmentMatches().
     */
    private List<String> normalizeEquipment(List<String> equipment) {
        if (equipment == null) return Collections.emptyList();
        return equipment.stream()
                .filter(e -> e != null && !e.isBlank())
                .map(e -> {
                    String key = e.trim().toLowerCase();
                    return EQUIPMENT_NORM.getOrDefault(key, key);
                })
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Returns true when the exercise's equipment value is acceptable given the
     * caller's normalized equipment list.
     *
     * Rules:
     *  - null / blank equipment on the exercise → always include (no equipment needed)
     *  - otherwise: check if the exercise equipment (lowercased) equals OR contains
     *    any requested value, or any requested value contains the exercise equipment.
     *    This handles partial matches like "e-z curl bar" ↔ "barbell".
     */
    private boolean equipmentMatches(String exerciseEquipment, List<String> requested) {
        if (exerciseEquipment == null || exerciseEquipment.isBlank()) return true;
        String exEq = exerciseEquipment.trim().toLowerCase();
        return requested.stream().anyMatch(req -> {
            String r = req.trim().toLowerCase();
            return exEq.equals(r) || exEq.contains(r) || r.contains(exEq);
        });
    }

    private List<ExerciseDTO> pickLevel(List<Exercise> pool, String level, int max, Set<String> used) {
        return pool.stream()
                .filter(e -> level.equalsIgnoreCase(e.getLevel()))
                .filter(e -> used.add(e.getId()))   // add() returns false if already present
                .limit(max)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ExerciseDTO toDTO(Exercise exercise) {
        String firstImage = (exercise.getImages() != null && !exercise.getImages().isEmpty())
                ? exercise.getImages().get(0)
                : null;

        String videoUrl = resolveVideoUrl(exercise.getName());

        return ExerciseDTO.builder()
                .name(exercise.getName())
                .level(exercise.getLevel())
                .equipment(exercise.getEquipment())
                .primaryMuscles(exercise.getPrimaryMuscles())
                .firstImage(firstImage)
                .instructions(exercise.getInstructions())
                .videoUrl(videoUrl)
                .build();
    }

    /**
     * Looks up a YouTube embed URL by checking if any key in VIDEO_MAP
     * is a case-insensitive substring of the exercise name.
     */
    private String resolveVideoUrl(String exerciseName) {
        if (exerciseName == null) return null;
        String nameLower = exerciseName.toLowerCase();
        return VIDEO_MAP.entrySet().stream()
                .filter(entry -> nameLower.contains(entry.getKey().toLowerCase()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private WorkoutResponseDTO emptyResponse() {
        Map<String, List<ExerciseDTO>> programs = new LinkedHashMap<>();
        programs.put("beginner",     Collections.emptyList());
        programs.put("intermediate", Collections.emptyList());
        programs.put("advanced",     Collections.emptyList());
        return WorkoutResponseDTO.builder().programs(programs).build();
    }
}
