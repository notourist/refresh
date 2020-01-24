package eu.kvfg.refresh.cover;

import eu.kvfg.refresh.grade.Grade;
import eu.kvfg.refresh.ldap.KvfgLdapUserDetails;
import eu.kvfg.refresh.util.Utils;
import eu.kvfg.refresh.util.cache.CacheController;
import eu.kvfg.refresh.util.cache.CacheResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Propagates the cached cover plan. Uses the {@link KvfgLdapUserDetails}
 * to get information about the user.
 *
 * @author Lukas Nasarek
 */
@RestController
@RequestMapping("/cover")
public class CoverController implements CacheController {

    private final CoverCache coverCache;

    @Value("${cover.remove-teacher-names}")
    private boolean removeTeacherNames;

    @Autowired
    public CoverController(CoverCache coverCache) {
        this.coverCache = coverCache;
    }

    /**
     * Returns the covers. Uses the class/grade or username.
     * Provides functionality used by the frontend admin tools.
     * <p>The following table can be used for a better
     * understanding of the choosing methods:
     * <table>
     * <tr>
     * <th>Administrators (depending on params)</th>
     * <th>Students</th>
     * <th>Teachers</th>
     * </tr>
     * <tr>
     * <th>
     * <table>
     * <th>default (no params)</th>
     * <th>all</th>
     * <th>teacher</th>
     * <th>grade</th>
     * <tr>
     * <th>{@link CoverCache#getChangesByGrade(Grade)}</th>
     * <th>{@link CoverCache#getCached()}</th>
     * <th>{@link CoverCache#getChangesByTeacher(String)}</th>
     * <th>{@link CoverCache#getChangesByGrade(Grade)}</th>
     * </tr>
     * </table>
     * </th>
     * <th>
     * <table>
     * <tr>
     * <th>removeTeacherNames=true</th>
     * <th>removeTeacherNames=false</th>
     * </tr>
     * <th>{@link CoverCache#getChangesByGrade(Grade)}</th>
     * <th>{@link CoverCache#getChangesByGradeCleaned(Grade)}</th>
     * </table>
     * </th>
     * <th>{@link CoverCache#getChangesByTeacher(String)}</th>
     * </tr>
     * </table>
     * </p>
     */
    @GetMapping
    public CacheResponse<Cover> cover(@RequestParam(required = false) boolean all,
                                      @RequestParam(required = false, defaultValue = "") String teacher,
                                      @RequestParam(required = false, defaultValue = "") String grade) {
        KvfgLdapUserDetails ldapUserDetails = (KvfgLdapUserDetails) Utils.getAuthenticationPrincipal();
        Grade userGrade = Grade.fromStudent(ldapUserDetails.getGradeLiteral());
        Collection<Cover> coverData;

        if (ldapUserDetails.isAdmin()) {
            if (all) {
                coverData = coverCache.getCached();
            } else if (!teacher.isEmpty()) {
                coverData = coverCache.getChangesByTeacher(teacher);
            } else if (!grade.isEmpty()) {
                coverData = coverCache.getChangesByGrade(Grade.fromStudent(grade));
            } else if (ldapUserDetails.isTeacher()) {
                coverData = coverCache.getChangesByTeacher(ldapUserDetails.getUsername());
            } else {
                coverData = coverCache.getChangesByGrade(userGrade);
            }
        } else if (ldapUserDetails.isTeacher()) {
            coverData = coverCache.getChangesByTeacher(ldapUserDetails.getUsername());
        } else {
            if (removeTeacherNames) {
                coverData = coverCache.getChangesByGradeCleaned(userGrade);
            } else {
                coverData = coverCache.getChangesByGrade(userGrade);
            }
        }
        return new CacheResponse<>(coverData, coverCache.getLastSuccessfulUpdate());
    }

    @GetMapping("/grades")
    public List<String> possibleGrades() {
        return getPossible(Cover::getGrades);
    }

    @GetMapping("/teachers")
    public List<String> possibleTeachers() {
        return getPossible(Cover::getTeachers);
    }

    private List<String> getPossible(Function<? super Cover, ? extends List<String>> mapper) {
        return coverCache.getCached()
            .stream()
            .map(mapper)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());

    }
}
