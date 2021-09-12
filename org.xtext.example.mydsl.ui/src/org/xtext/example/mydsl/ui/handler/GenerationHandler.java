package org.xtext.example.mydsl.ui.handler;


import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.commands.AbstractHandler;
import org.eclipse.ui.commands.ExecutionException;
import org.eclipse.ui.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.eclipse.xtext.generator.IFileSystemAccess2;

public class GenerationHandler extends AbstractHandler implements IHandler {
    
    @Inject
    private IGenerator generator;
 
    @Inject
    private Provider<EclipseResourceFileSystemAccess> fileAccessProvider;
     
    @Inject
    IResourceDescriptions resourceDescriptions;
     
    @Inject
    IResourceSetProvider resourceSetProvider;
     
    @Override
    public Object execute(ExecutionEvent event){
         
        IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
        IFile file = (IFile) activeEditor.getEditorInput().getAdapter(IFile.class);
        if (file != null) {
            IProject project = file.getProject();
            IFolder srcGenFolder = project.getFolder("src-gen");
            if (!srcGenFolder.exists()) {
                try {
                    srcGenFolder.create(true, true,
                            new NullProgressMonitor());
                } catch (CoreException e) {
                    return null;
                }
            }
     
            final EclipseResourceFileSystemAccess fsa = fileAccessProvider.get();
            fsa.setOutputPath(srcGenFolder.getFullPath().toString());
             
             
            if (activeEditor instanceof XtextEditor) {
                ((XtextEditor)activeEditor).getDocument().readOnly(new IUnitOfWork<Boolean, XtextResource>() {
                 
                    @Override
                    public Boolean exec(XtextResource state)
                            throws Exception {
                        generator.doGenerate(state, fsa);
                        return Boolean.TRUE;
                    }
                });
                 
            }
        }
        return null;
    }
 
    @Override
    public boolean isEnabled() {
        return true;
    }

	@Override
	public Object execute(Map arg0) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}
 
}
