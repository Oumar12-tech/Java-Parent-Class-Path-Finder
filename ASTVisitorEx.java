
package p.actions;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ASTVisitorEx extends ASTVisitor {
	HashMap<String, String> parentMap = new HashMap<>();

	@Override
    public boolean visit(TypeDeclaration node) {
        String className = node.getName().getIdentifier();
        if (node.getSuperclassType() != null) {
        	String parentClassName = node.getSuperclassType().toString();
        	parentMap.put(className, parentClassName);
        }else {
        	parentMap.put(className, null);
        }
        return super.visit(node);
    }
}
