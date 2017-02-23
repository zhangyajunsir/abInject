Governator 是一个用来增强 Google Guice 框架的库、扩展和工具集。
提供：类路径扫描和自动绑定、生命周期管理、配置到字段的映射、字段验证和并行对象热身 (parallelized object warmup)。
https://github.com/Netflix/governator/
使用了governator(1.10.5)同时合并了governator-annotations的全部内容和governator-core的lazy包内容
该扩展框架依赖Jar：
javax.inject-1.jar 依赖注入规范
jackson-databind-2.4.3.jar jackson-annotations-2.4.3.jar jackson-core-2.4.3.jar(合并至com.fasterxml.jackson插件) json处理工具
slf4j-api-1.6.3.jar 日志门面（平台使用的slf4j1.7.5）
guice-4.0.jar guice-multibindings-4.0.jar guice-grapher-4.0.jar guice-assistedinject-4.0.jar(合并至com.google.inject插件) google的依赖注入实现
asm-5.0.4.jar(com.google.inject插件包含并提供该jar功能) ASM是一个Java字节码操控框架。它能被用来动态生成类或者增强既有类的功能。ASM可以直接产生二进制 class 文件，也可以在类被加载入Java虚拟机之前动态改变类行为。
validation-api-1.0.0.GA.jar 字段验证规范；hibernate-validator-4.1.0.Final.jar hibernate实现的字段验证工具包(合并至java.validation插件)


governator开源框架修改：
	guice\Grapher.java 泛型的使用方式，原来必须在1.7环境编译
	guice\InternalAutoBindModule.java 修改较多
	guice\InternalAutoBindModuleBootstrapModule.java 访问权限修改
	guice\InternalBootstrapModule.java 访问权限修改
	guice\InternalLifecycleModule.java 访问权限修改
	guice\LifecycleInjectorBuilderImpl.java 访问权限修改
	guice\MemberInjectingInstanceProvider.java 访问权限修改
	guice\ModuleListBuilder.java 访问权限修改
	guice\ModulesEx.java 泛型的使用方式，原来必须在1.7环境编译
	guice\ProviderBinderUtil.java 访问权限修改
	guice\ModuleListBuilder.java 添加清理已解析的 clear
	lifecycle\AnnotationFinder.java 修改较多
	lifecycle\ClasspathScanner.java 修改较多
	lifecycle\DirectoryClassFilter.java 访问权限修改
	lifecycle\LifecycleManager.java 去除对warmup功能的调用
governator开源框架删除：warmup功能详见https://github.com/Netflix/governator/wiki/Warm-Up
	lifecycle\warmup\WarmUpSession.java 使用1.7的并发api
	lifecycle\warmup\WarmUpTask.java 使用1.7的并发api


技术理解：
guice-multibindings是提供了Set和Map类型的映射
	使用时：
@Inject @Named("work")
private Set<Runnable> work;
@Inject @Named("otherWork");
private Set<Runnable> otherWork;
	映射时：
Multibinder<Runnable> multibinder = Multibinder.newSetBinder(binder(), Runnable.class, Names.named("work"));



