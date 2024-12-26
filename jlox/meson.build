project('jlox', 'java',
  version : '0.1',
  default_options : ['warning_level=3'])

sources = ['src/Jlox.java', 'src/Test.java']

exe = jar('jlox', sources,
  main_class : 'Jlox',
  install : true)

test('basic', exe)
