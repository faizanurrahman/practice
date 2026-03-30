package com.practice.todo;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = TodoApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureModulesTest {

	@ArchTest
	static final ArchRule notificationDoesNotDependOnTask = noClasses()
			.that()
			.resideInAPackage("..modules.notification..")
			.should()
			.dependOnClassesThat()
			.resideInAnyPackage("..modules.task..");

	@ArchTest
	static final ArchRule moduleSlicesFreeOfCycles = slices()
			.matching("com.practice.todo.modules.(*)..")
			.should()
			.beFreeOfCycles();

	@ArchTest
	static final ArchRule controllersDoNotDependOnRepositories = noClasses()
			.that()
			.haveSimpleNameEndingWith("Controller")
			.should()
			.dependOnClassesThat()
			.haveSimpleNameEndingWith("Repository");

	@ArchTest
	static final ArchRule nonInfrastructureDoesNotDependOnInfrastructure = noClasses()
			.that()
			.resideInAPackage("..modules..")
			.and()
			.resideOutsideOfPackage("..infrastructure..")
			.and()
			.resideOutsideOfPackage("..modules.search..")
			.and()
			.resideOutsideOfPackage("..modules.comments..")
			.and()
			.resideOutsideOfPackage("..modules.content..")
			.and()
			.resideOutsideOfPackage("..modules.presence..")
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..infrastructure..");
}
