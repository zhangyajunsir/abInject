Governator ��һ��������ǿ Google Guice ��ܵĿ⡢��չ�͹��߼���
�ṩ����·��ɨ����Զ��󶨡��������ڹ������õ��ֶε�ӳ�䡢�ֶ���֤�Ͳ��ж������� (parallelized object warmup)��
https://github.com/Netflix/governator/
ʹ����governator(1.10.5)ͬʱ�ϲ���governator-annotations��ȫ�����ݺ�governator-core��lazy������
����չ�������Jar��
javax.inject-1.jar ����ע��淶
jackson-databind-2.4.3.jar jackson-annotations-2.4.3.jar jackson-core-2.4.3.jar(�ϲ���com.fasterxml.jackson���) json������
slf4j-api-1.6.3.jar ��־���棨ƽ̨ʹ�õ�slf4j1.7.5��
guice-4.0.jar guice-multibindings-4.0.jar guice-grapher-4.0.jar guice-assistedinject-4.0.jar(�ϲ���com.google.inject���) google������ע��ʵ��
asm-5.0.4.jar(com.google.inject����������ṩ��jar����) ASM��һ��Java�ֽ���ٿؿ�ܡ����ܱ�������̬�����������ǿ������Ĺ��ܡ�ASM����ֱ�Ӳ��������� class �ļ���Ҳ�������౻������Java�����֮ǰ��̬�ı�����Ϊ��
validation-api-1.0.0.GA.jar �ֶ���֤�淶��hibernate-validator-4.1.0.Final.jar hibernateʵ�ֵ��ֶ���֤���߰�(�ϲ���java.validation���)


governator��Դ����޸ģ�
	guice\Grapher.java ���͵�ʹ�÷�ʽ��ԭ��������1.7��������
	guice\InternalAutoBindModule.java �޸Ľ϶�
	guice\InternalAutoBindModuleBootstrapModule.java ����Ȩ���޸�
	guice\InternalBootstrapModule.java ����Ȩ���޸�
	guice\InternalLifecycleModule.java ����Ȩ���޸�
	guice\LifecycleInjectorBuilderImpl.java ����Ȩ���޸�
	guice\MemberInjectingInstanceProvider.java ����Ȩ���޸�
	guice\ModuleListBuilder.java ����Ȩ���޸�
	guice\ModulesEx.java ���͵�ʹ�÷�ʽ��ԭ��������1.7��������
	guice\ProviderBinderUtil.java ����Ȩ���޸�
	guice\ModuleListBuilder.java ��������ѽ����� clear
	lifecycle\AnnotationFinder.java �޸Ľ϶�
	lifecycle\ClasspathScanner.java �޸Ľ϶�
	lifecycle\DirectoryClassFilter.java ����Ȩ���޸�
	lifecycle\LifecycleManager.java ȥ����warmup���ܵĵ���
governator��Դ���ɾ����warmup�������https://github.com/Netflix/governator/wiki/Warm-Up
	lifecycle\warmup\WarmUpSession.java ʹ��1.7�Ĳ���api
	lifecycle\warmup\WarmUpTask.java ʹ��1.7�Ĳ���api


������⣺
guice-multibindings���ṩ��Set��Map���͵�ӳ��
	ʹ��ʱ��
@Inject @Named("work")
private Set<Runnable> work;
@Inject @Named("otherWork");
private Set<Runnable> otherWork;
	ӳ��ʱ��
Multibinder<Runnable> multibinder = Multibinder.newSetBinder(binder(), Runnable.class, Names.named("work"));



