package yjh.ontongsal.common.web.swagger.scanner.auto;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AsmResultCodeDetector {

    private final Set<String> detectResultCodeNames = new HashSet<>();
    private final Set<String> visitedMethods = new HashSet<>();
    private final Map<String, ClassNode> classCache = new HashMap<>();

    private final List<String> targetClassPaths;
    private final Map<String, List<String>> abstractMap;
    private final Map<String, List<String>> interfaceMap;

    private AsmResultCodeDetector(Builder builder) {
        this.targetClassPaths = builder.targetClassPaths;
        this.abstractMap = builder.abstractMap;
        this.interfaceMap = builder.interfaceMap;
    }

    public static Builder builder(List<String> targetClassPaths) {
        return new Builder(targetClassPaths);
    }

    public Set<String> thenGetDetectResultCodeNames() {
        return detectResultCodeNames;
    }

    public AsmResultCodeDetector analyze(String className, String methodName) throws IOException {
        ClassNode classNode = getClassNode(className);
        if (classNode == null) {
            return this;
        }

        for (MethodNode method : classNode.methods) {
            if (method.name.equals(methodName))
                analyzeMethod(classNode.name, method);
        }
        return this;
    }

    private void analyzeMethod(String classInternalName, MethodNode method) {
        String methodKey = classInternalName + "." + method.name + method.desc;

        if (!visitedMethods.add(methodKey)) {
            return;
        }

        InsnList instructions = method.instructions;
        for (AbstractInsnNode insn : instructions) {
            if (insn instanceof MethodInsnNode methodInsn) {
                String internalName = methodInsn.owner;
                if (internalName.startsWith("[") || internalName.startsWith("java/")) {
                    continue;
                }

                String fqName = internalName.replace('/', '.');
                try {
                    analyze(fqName, methodInsn.name);

                    List<String> subclasses = abstractMap.get(fqName);
                    if (subclasses != null) {
                        for (String sub : subclasses) {
                            analyze(sub.replace('/', '.'), methodInsn.name);
                        }
                    }

                    List<String> implementors = interfaceMap.get(fqName);
                    if (implementors != null) {
                        for (String impl : implementors) {
                            analyze(impl.replace('/', '.'), methodInsn.name);
                        }
                    }
                } catch (IOException ignored) {
                }
            }

            if (insn instanceof InvokeDynamicInsnNode indyInsn) {
                for (Object bsmArg : indyInsn.bsmArgs) {
                    if (!(bsmArg instanceof Handle handle)) {
                        continue;
                    }
                    String owner = handle.getOwner();
                    if (owner.startsWith("[") || owner.startsWith("java/")) {
                        continue;
                    }
                    try {
                        analyze(owner.replace('/', '.'), handle.getName());
                    } catch (IOException ignored) {
                    }
                }
            }

            if (insn instanceof FieldInsnNode fieldInsn) {
                targetClassPaths.stream()
                        .filter(target -> fieldInsn.owner.equals(target.replace('.', '/')))
                        .map(target -> target + "." + fieldInsn.name)
                        .forEach(detectResultCodeNames::add);
            }
        }
    }

    private ClassNode getClassNode(String className) throws IOException {
        var cached = classCache.get(className);
        if (cached != null) return cached;

        String path = className.replace('.', '/') + ".class";
        InputStream in = AsmResultCodeDetector.class.getClassLoader().getResourceAsStream(path);
        if (in == null) {
            return null;
        }

        ClassReader reader = new ClassReader(in);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.SKIP_DEBUG);
        classCache.put(className, classNode);

        return classNode;
    }

    public static class Builder {
        private final List<String> targetClassPaths;
        private Map<String, List<String>> abstractMap = new HashMap<>();
        private Map<String, List<String>> interfaceMap = new HashMap<>();

        public Builder(List<String> targetClassPaths) {
            this.targetClassPaths = targetClassPaths;
        }

        public Builder abstractMap(Map<String, List<String>> abstractMap) {
            this.abstractMap = abstractMap;
            return this;
        }

        public Builder interfaceMap(Map<String, List<String>> interfaceMap) {
            this.interfaceMap = interfaceMap;
            return this;
        }

        public AsmResultCodeDetector build() {
            return new AsmResultCodeDetector(this);
        }
    }
}
