project('jlox', 'java',
  version : '0.1',
  default_options : ['warning_level=3'])

sources = ['com/craftinginterpreters/lox/Lox.java', 'com/craftinginterpreters/lox/Test.java']

java_args = ['-g']

exe = jar('jlox', sources,
  main_class : 'com.craftinginterpreters.lox.Lox',
  install : true,
  java_args : java_args)

test('basic', exe)
