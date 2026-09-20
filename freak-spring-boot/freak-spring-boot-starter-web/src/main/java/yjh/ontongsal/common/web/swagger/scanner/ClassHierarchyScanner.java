package yjh.ontongsal.common.web.swagger.scanner;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassHierarchyScanner {
    private static final Logger log = LoggerFactory.getLogger(ClassHierarchyScanner.class);

    private ClassHierarchyScanner() {
    }

    public static Map<String, List<String>> createAbstractSubclassMap(List<String> basePackages) {
        long start = System.nanoTime();
        Map<String, List<String>> result = new LinkedHashMap<>();

        for (String basePackage : basePackages) {
            try (ScanResult scanResult = new ClassGraph()
                    .acceptPackages(basePackage)
                    .enableClassInfo()
                    .scan()
            ) {
                scanResult.getAllClasses().stream()
                        .filter(ClassInfo::isAbstract)
                        .filter(ci -> !ci.isInterface())
                        .forEach(abstractClass -> {
                            var subclasses = abstractClass.getSubclasses().stream()
                                    .filter(sub -> !sub.isAbstract())
                                    .map(ClassInfo::getName)
                                    .toList();
                            if (!subclasses.isEmpty()) {
                                result.put(abstractClass.getName(), subclasses);
                            }
                        });
            }
        }

        log.debug("createAbstractSubclassMap: {} ms", (System.nanoTime() - start) / 1_000_000);
        return result;
    }

    public static Map<String, List<String>> createInterfaceImplementationMap(List<String> basePackages) {
        long start = System.nanoTime();
        Map<String, List<String>> result = new LinkedHashMap<>();

        for (String basePackage : basePackages) {
            try (ScanResult scanResult = new ClassGraph()
                    .acceptPackages(basePackage)
                    .enableClassInfo()
                    .scan()
            ) {
                scanResult.getAllClasses().stream()
                        .filter(ClassInfo::isInterface)
                        .forEach(iface -> {
                            var implementors = iface.getClassesImplementing().stream()
                                    .filter(impl -> !impl.isAbstract())
                                    .map(ClassInfo::getName)
                                    .toList();
                            if (!implementors.isEmpty()) {
                                result.put(iface.getName(), implementors);
                            }
                        });
            }
        }

        log.debug("createInterfaceImplementationMap: {} ms", (System.nanoTime() - start) / 1_000_000);
        return result;
    }
}
