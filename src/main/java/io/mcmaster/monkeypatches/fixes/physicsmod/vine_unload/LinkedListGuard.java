package io.mcmaster.monkeypatches.fixes.physicsmod.vine_unload;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Link checks for Physics Mod's {@code net.diebuddies.util.DoublyLinkedList},
 * used by {@code DoublyLinkedListMixin}.
 *
 * Physics Mod is closed source and not available at compile time, so the
 * list's private fields and its nested {@code Node} / {@code NodeStorage}
 * types are reached through a {@link MethodHandles.Lookup} created inside the
 * list class itself (the mixin's static initializer runs in the target class).
 */
public final class LinkedListGuard {
    private final VarHandle head;
    private final VarHandle tail;
    private final VarHandle prev;
    private final VarHandle next;
    private final VarHandle data;
    private final MethodHandle getNode;
    private final MethodHandle setNode;

    private LinkedListGuard(VarHandle head, VarHandle tail, VarHandle prev, VarHandle next, VarHandle data,
            MethodHandle getNode, MethodHandle setNode) {
        this.head = head;
        this.tail = tail;
        this.prev = prev;
        this.next = next;
        this.data = data;
        this.getNode = getNode;
        this.setNode = setNode;
    }

    /**
     * @param listLookup a lookup with private access to
     *                   {@code net.diebuddies.util.DoublyLinkedList}
     */
    public static LinkedListGuard create(MethodHandles.Lookup listLookup) {
        try {
            Class<?> listClass = listLookup.lookupClass();
            ClassLoader loader = listClass.getClassLoader();
            Class<?> nodeClass = Class.forName(listClass.getName() + "$Node", false, loader);
            Class<?> storageClass = Class.forName(listClass.getName() + "$NodeStorage", false, loader);

            return new LinkedListGuard(
                    listLookup.findVarHandle(listClass, "head", nodeClass),
                    listLookup.findVarHandle(listClass, "tail", nodeClass),
                    listLookup.findVarHandle(nodeClass, "prev", nodeClass),
                    listLookup.findVarHandle(nodeClass, "next", nodeClass),
                    listLookup.findVarHandle(nodeClass, "data", Object.class),
                    listLookup.findVirtual(storageClass, "getNode", MethodType.methodType(nodeClass)),
                    listLookup.findVirtual(storageClass, "setNode", MethodType.methodType(void.class, nodeClass)));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unexpected Physics Mod DoublyLinkedList structure", e);
        }
    }

    /**
     * Returns whether {@code node} looks linked into {@code list}: it is the
     * head or tail, or both of its neighbours point back at it. A {@code null}
     * node is reported as linked so the original code handles it.
     *
     * This is an O(1) neighbour check, not a full membership test: a middle
     * node of some other intact chain also passes. That's enough here because
     * {@link #detachAll} unlinks nodes on every {@code clear()}, so the stale
     * first/last nodes that crash {@code removeNode} are the only ones left.
     */
    public boolean isLinked(Object list, Object node) {
        if (node == null || node == head.get(list) || node == tail.get(list)) {
            return true;
        }

        Object before = prev.get(node);
        Object after = next.get(node);
        return before != null && after != null && next.get(before) == node && prev.get(after) == node;
    }

    /**
     * Unlinks every node still reachable from the list's head and clears the
     * stored node on each element that still points at it, so elements removed
     * after {@code clear()} are treated as not being in the list.
     */
    public void detachAll(Object list) {
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Object current = head.get(list);

        while (current != null && visited.add(current)) {
            Object following = next.get(current);
            Object element = data.get(current);

            try {
                if (element != null && getNode.invoke(element) == current) {
                    setNode.invoke(element, null);
                }
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to detach Physics Mod list node", t);
            }

            prev.set(current, null);
            next.set(current, null);
            current = following;
        }
    }
}
