package innovent.birt.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionExecutor;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.impl.ReportRunnable;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.script.internal.ReportContextImpl;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.LibraryHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.command.ExtendsException;
import org.eclipse.birt.report.model.api.metadata.IElementDefn;
import org.eclipse.birt.report.model.api.metadata.IElementPropertyDefn;
import org.eclipse.birt.report.model.core.DesignElement;
import org.eclipse.birt.report.model.core.Module;
import org.eclipse.birt.report.model.elements.Label;
import org.eclipse.birt.report.model.elements.Library;
import org.eclipse.birt.report.model.elements.ReportDesign;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import innovent.birt.functions.AppendLibraryContent;

public class AppendLibraryContentTest {

	// Note: Mockito cannot mock fields. Context is accessed via reflection.
	private static class MockReportContext extends ReportContextImpl {
		@SuppressWarnings("unused")
		public final ExecutionContext context;

		public MockReportContext(ExecutionContext context) {
			super(context);
			this.context = context;
		}
	}

	// Cannot mock, need field access
	private static class MockReportDesignHandle extends ReportDesignHandle {
		private ElementFactory elementFactory;
		private SlotHandle body;

		public MockReportDesignHandle(ReportDesign design, ElementFactory elementFactory, SlotHandle body) {
			super(design);
			this.elementFactory = elementFactory;
			this.body = body;
		}

		@Override
		public ElementFactory getElementFactory() {
			return elementFactory;
		}

		@Override
		public SlotHandle getBody() {
			return body;
		}
	}

	// 10/28/2020 this doesn't work
	@Test
	public void testExecute() {
		IScriptFunctionContext scriptContext = Mockito.mock(IScriptFunctionContext.class);
		ExecutionContext executionContext = Mockito.mock(ExecutionContext.class);
		ReportRunnable runnable = Mockito.mock(ReportRunnable.class);
		ReportDesign reportDesign = Mockito.mock(ReportDesign.class);

		Label label = Mockito.mock(Label.class);
		IElementDefn labelDefn = Mockito.mock(IElementDefn.class);
		Mockito.when(labelDefn.getSlotCount()).thenReturn(0);
		Mockito.when(label.getDefn()).thenReturn(labelDefn);

		Library library = Mockito.mock(Library.class);
		IElementDefn libraryDefn = Mockito.mock(IElementDefn.class);
		Mockito.when(libraryDefn.getSlotCount()).thenReturn(0);
		Mockito.when(library.getDefn()).thenReturn(libraryDefn);
		List<IElementPropertyDefn> propertyDefns = new ArrayList<IElementPropertyDefn>();
		Mockito.when(library.getPropertyDefns()).thenReturn(propertyDefns);

		LabelHandle labelHandle = new LabelHandle(library, label);
		DesignElement elementDE = Mockito.mock(DesignElement.class);
		Mockito.when(elementDE.getHandle(Mockito.any(Module.class))).thenReturn(labelHandle);
		// this returns null
		System.out.println("AppendLibraryContent: library.findElement(\"TestLibraryElement\") = " + library.findElement("TestLibraryElement"));
		Mockito.when(library.findElement("TestLibraryElement")).thenReturn(elementDE);
		// this returns something
		System.out.println("AppendLibraryContent: library.findElement(\"TestLibraryElement\") = " + library.findElement("TestLibraryElement"));
		// Note: Cannot mock ModuleHandle because Mockito cannot mock final
		// methods
		LibraryHandle libraryHandle = new LibraryHandle(library);
		Mockito.when(library.handle()).thenReturn(libraryHandle);

		// final DesignElementHandle tleh =
		// Mockito.mock(DesignElementHandle.class);
		List<Library> listOfLibraries = new ArrayList<Library>();
		listOfLibraries.add(library);
		Mockito.when(reportDesign.getLibraries()).thenReturn(listOfLibraries);
		IElementDefn reportDesignDefn = Mockito.mock(IElementDefn.class);
		Mockito.when(reportDesignDefn.getSlotCount()).thenReturn(0);
		Mockito.when(reportDesign.getDefn()).thenReturn(reportDesignDefn);
		Mockito.when(elementDE.getRoot()).thenReturn(reportDesign);
		ElementFactory elementFactory = Mockito.mock(ElementFactory.class);
		try {
			Mockito.when(elementFactory.newElementFrom(Mockito.any(DesignElementHandle.class), Mockito.anyString()))
					.thenReturn(labelHandle);
		} catch (ExtendsException e1) {
			Assert.fail(e1.toString());
		}
		SlotHandle bodySlotHandle = Mockito.mock(SlotHandle.class);
		MockReportDesignHandle reportDesignHandle = new MockReportDesignHandle(reportDesign, elementFactory,
				bodySlotHandle);

		Mockito.when(runnable.getDesignHandle()).thenReturn(reportDesignHandle);
		IEngineTask engineTask = Mockito.mock(IEngineTask.class);
		Mockito.when(executionContext.getEngineTask()).thenReturn(engineTask);
		Mockito.when(executionContext.getRunnable()).thenReturn(runnable);
		IReportContext reportContext = new MockReportContext(executionContext);
		Mockito.when(reportContext.getReportRunnable()).thenReturn(runnable);
		IScriptFunctionExecutor sfe = new AppendLibraryContent();
		try {
			// Note: isDataSetEditor works because mockito creates a class that
			// is NOT named a certain way.
			// 10/28/2020 this cannot find element despite library being able to find it.
			sfe.execute(new Object[] { "TestLibraryElement", reportContext }, scriptContext);
		} catch (BirtException e) {
			Assert.fail(e.toString());
		}
	}
}
