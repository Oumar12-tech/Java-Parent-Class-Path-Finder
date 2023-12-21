package p.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class RunAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public RunAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	@Override
	public void run(IAction action) {

		IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot iWorkspaceRoot = iWorkspace.getRoot();
		IProject[] iProjectList = iWorkspaceRoot.getProjects();
		for(IProject iProject : iProjectList) {			
			List<ICompilationUnit> iCUs = new ArrayList<ICompilationUnit>();
			IJavaProject iJavaProject = JavaCore.create(iProject);
	
			try {
				IPackageFragment[] iPackageFragmentList = iJavaProject.getPackageFragments();
				for (IPackageFragment iPackageFragment : iPackageFragmentList) {
					if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
						continue;
					}
	
					ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
					for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
						iCUs.add(iCompilationUnit);
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
	
			ICompilationUnit[] compUnits = iCUs.toArray(new ICompilationUnit[0]);
			final Map<ICompilationUnit, ASTNode> parsedCompilationUnits = new HashMap<ICompilationUnit, ASTNode>();
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setResolveBindings(true);
			parser.setEnvironment(null, null, null, true);
			parser.setProject(iJavaProject);
			parser.createASTs(compUnits, new String[0], new ASTRequestor() {
				@Override
				public final void acceptAST(final ICompilationUnit unit, final CompilationUnit node) {
					parsedCompilationUnits.put(unit, node);
				}
	
				@Override
				public final void acceptBinding(final String key, final IBinding binding) {
					// Do nothing
				}
			}, null);
			
			ASTVisitorEx astVisitorEx = new ASTVisitorEx();
			Iterator<ICompilationUnit> keySetIterator = parsedCompilationUnits.keySet().iterator();
			while (keySetIterator.hasNext()) {
				ICompilationUnit iCU = keySetIterator.next();
				CompilationUnit cu = (CompilationUnit) parsedCompilationUnits.get(iCU);
				/**
				 * Assignment #5
				 * 
				 * On my honor, <Your Full Name>, this assignment is my own work.  
				 * I, <Your Full Name>, will follow the instructor's rules and processes 
				 * related to academic integrity as directed in the course syllabus.
				 *
				 */
				
				//---------------------------------------------------------------------
				//
				
				cu.accept(astVisitorEx);
				
			}//while
			
			try {		
				File file = new File("input.txt");
				Scanner sc = new Scanner(file);
				
				//print out input for debugging
				HashMap<String, String> parentMap = astVisitorEx.parentMap;
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					String[] classNames = line.split("");
					String originClassName = classNames[0];
					String destinationClassName = classNames[1];

					
					if (!parentMap.containsKey(originClassName) && !parentMap.containsValue(originClassName)) {
						System.out.println("E");
					} else if (originClassName.equals(destinationClassName)) {
						System.out.println("");// Origin and destination are the same class
					} else {
						// search from origin to destination
						String pathFromOrigin = traversePath(parentMap, originClassName, destinationClassName, false);
						if (pathFromOrigin.equals("N")) {

							// search from destination to origin
							String pathFromDestination = traversePath(parentMap, destinationClassName, originClassName,
									true);

							// if origin and destination are sibling
							if (pathFromDestination.equals("N")) {
								String commonParent = findCommonParent(parentMap, originClassName,
										destinationClassName);
								// if no common parent was found then there is no path available
								if (!commonParent.equals("N")) {
									String pathToParent = traversePath(parentMap, originClassName, commonParent, false);
									String pathFromParent = traversePath(parentMap, destinationClassName, commonParent,
											true);
									System.out.println(pathToParent + pathFromParent);
								} else {
									System.out.println("N");
								}
							} else {
								System.out.println(pathFromDestination);
							}
						} else {
							System.out.println(pathFromOrigin);
						}

					}
				}

				sc.close();
			} catch (FileNotFoundException e) {
				System.out.println(e);
				System.out.println("ERROR - cannot open front.in \n");
			}
			
			
		}//for
	}
	
	private String findCommonParent(HashMap<String, String> parentMap, String originClassName,
			String destinationClassName) {
		Set<String> originAncestors = getAllAncestors(parentMap,originClassName);
		Set<String> destinationAncestors = getAllAncestors(parentMap,destinationClassName);
		originAncestors.retainAll(destinationAncestors);
		if (!originAncestors.isEmpty()) {
            // Common parent found, return the first one in the set
            return originAncestors.iterator().next();
        }
		return "N";
	}

	private Set<String> getAllAncestors(HashMap<String, String> parentMap, String className) {
		Set<String> ancestors = new LinkedHashSet<>();
        String parent = parentMap.get(className);

        while (parent != null) {
            ancestors.add(parent);
            parent = parentMap.get(parent);
        }

        return ancestors;
	}

	private String traversePath(HashMap<String, String> parentMap, String originClassName, String destinationClassName,boolean reverse) {
		String path = "";
        String currentClass = originClassName;

        while (!currentClass.equals(destinationClassName)) {
            if (!parentMap.containsKey(currentClass)) {
                return "E"; 
            }
            if(parentMap.get(currentClass)== null) {
            	return "N"; // No path between startClass and destinationClass
            }

            String nextClass = parentMap.get(currentClass);
            if(reverse) {
            	 path += "D";
            }else {
            	 path += "U";
            }
            // Move up to parent class
            currentClass = nextClass;
        }

        return path;
	}

	//run
	

	
		//
	//---------------------------------------------------------------------

	/**
	 * Selection in the workbench has been changed. We can change the state of the
	 * 'real' action here if we want, but this can only happen after the delegate
	 * has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	@Override
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell for
	 * the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}