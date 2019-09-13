"""Utilities for processing .test files containing test case descriptions."""

import os.path
import os
import posixpath
import re
from os import remove, rmdir
import shutil

import pytest  # type: ignore  # no pytest in typeshed
from typing import Callable, List, Tuple, Set, Optional, Iterator, Any, Dict, NamedTuple, Union
import typing

from mypy.myunit import ProtoTestCase
from mypy.test.config import test_data_prefix

root_dir = os.path.normpath(os.path.join(os.path.dirname(__file__), '..', '..'))

# File modify/create operation: copy module contents from source_path.
UpdateFile = NamedTuple('UpdateFile', [('module', str),
                                       ('source_path', str),
                                       ('target_path', str)])

# File delete operation: delete module file.
DeleteFile = NamedTuple('DeleteFile', [('module', str),
                                       ('path', str)])

FileOperation = Union[UpdateFile, DeleteFile]


def parse_test_cases(
        path: str,
        base_path: str = '.',
        optional_out: bool = False,
        native_sep: bool = False) -> List['DataDrivenTestCase']:
    """Parse a file with test case descriptions.

    Return an array of test cases.

    NB: this function and DataDrivenTestCase were shared between the
    myunit and pytest codepaths -- if something looks redundant,
    that's likely the reason.
    """
    if native_sep:
        join = os.path.join
    else:
        join = posixpath.join  # type: ignore
    include_path = os.path.dirname(path)
    with open(path, encoding='utf-8') as f:
        lst = f.readlines()
    for i in range(len(lst)):
        lst[i] = lst[i].rstrip('\n')
    p = parse_test_data(lst, path)
    out = []  # type: List[DataDrivenTestCase]

    # Process the parsed items. Each item has a header of form [id args],
    # optionally followed by lines of text.
    i = 0
    while i < len(p):
        ok = False
        i0 = i
        if p[i].id == 'case':
            i += 1

            files = []  # type: List[Tuple[str, str]] # path and contents
            output_files = []  # type: List[Tuple[str, str]] # path and contents for output files
            tcout = []  # type: List[str]  # Regular output errors
            tcout2 = {}  # type: Dict[int, List[str]]  # Output errors for incremental, runs 2+
            deleted_paths = {}  # type: Dict[int, Set[str]]  # from run number of paths
            stale_modules = {}  # type: Dict[int, Set[str]]  # from run number to module names
            rechecked_modules = {}  # type: Dict[ int, Set[str]]  # from run number module names
            while i < len(p) and p[i].id != 'case':
                if p[i].id == 'file' or p[i].id == 'outfile':
                    # Record an extra file needed for the test case.
                    arg = p[i].arg
                    assert arg is not None
                    contents = '\n'.join(p[i].data)
                    contents = expand_variables(contents)
                    file_entry = (join(base_path, arg), contents)
                    if p[i].id == 'file':
                        files.append(file_entry)
                    elif p[i].id == 'outfile':
                        output_files.append(file_entry)
                elif p[i].id in ('builtins', 'builtins_py2'):
                    # Use an alternative stub file for the builtins module.
                    arg = p[i].arg
                    assert arg is not None
                    mpath = join(os.path.dirname(path), arg)
                    if p[i].id == 'builtins':
                        fnam = 'builtins.pyi'
                    else:
                        # Python 2
                        fnam = '__builtin__.pyi'
                    with open(mpath) as f:
                        files.append((join(base_path, fnam), f.read()))
                elif p[i].id == 'typing':
                    # Use an alternative stub file for the typing module.
                    arg = p[i].arg
                    assert arg is not None
                    src_path = join(os.path.dirname(path), arg)
                    with open(src_path) as f:
                        files.append((join(base_path, 'typing.pyi'), f.read()))
                elif re.match(r'stale[0-9]*$', p[i].id):
                    if p[i].id == 'stale':
                        passnum = 1
                    else:
                        passnum = int(p[i].id[len('stale'):])
                        assert passnum > 0
                    arg = p[i].arg
                    if arg is None:
                        stale_modules[passnum] = set()
                    else:
                        stale_modules[passnum] = {item.strip() for item in arg.split(',')}
                elif re.match(r'rechecked[0-9]*$', p[i].id):
                    if p[i].id == 'rechecked':
                        passnum = 1
                    else:
                        passnum = int(p[i].id[len('rechecked'):])
                    arg = p[i].arg
                    if arg is None:
                        rechecked_modules[passnum] = set()
                    else:
                        rechecked_modules[passnum] = {item.strip() for item in arg.split(',')}
                elif p[i].id == 'delete':
                    # File to delete during a multi-step test case
                    arg = p[i].arg
                    assert arg is not None
                    m = re.match(r'(.*)\.([0-9]+)$', arg)
                    assert m, 'Invalid delete section: {}'.format(arg)
                    num = int(m.group(2))
                    assert num >= 2, "Can't delete during step {}".format(num)
                    full = join(base_path, m.group(1))
                    deleted_paths.setdefault(num, set()).add(full)
                elif p[i].id == 'out' or p[i].id == 'out1':
                    tcout = p[i].data
                    tcout = [expand_variables(line) for line in tcout]
                    if os.path.sep == '\\':
                        tcout = [fix_win_path(line) for line in tcout]
                    ok = True
                elif re.match(r'out[0-9]*$', p[i].id):
                    passnum = int(p[i].id[3:])
                    assert passnum > 1
                    output = p[i].data
                    output = [expand_variables(line) for line in output]
                    if native_sep and os.path.sep == '\\':
                        output = [fix_win_path(line) for line in output]
                    tcout2[passnum] = output
                    ok = True
                else:
                    raise ValueError(
                        'Invalid section header {} in {} at line {}'.format(
                            p[i].id, path, p[i].line))
                i += 1

            for passnum in stale_modules.keys():
                if passnum not in rechecked_modules:
                    # If the set of rechecked modules isn't specified, make it the same as the set
                    # of modules with a stale public interface.
                    rechecked_modules[passnum] = stale_modules[passnum]
                if (passnum in stale_modules
                        and passnum in rechecked_modules
                        and not stale_modules[passnum].issubset(rechecked_modules[passnum])):
                    raise ValueError(
                        ('Stale modules after pass {} must be a subset of rechecked '
                         'modules ({}:{})').format(passnum, path, p[i0].line))

            if optional_out:
                ok = True

            if ok:
                input = expand_includes(p[i0].data, include_path)
                expand_errors(input, tcout, 'main')
                for file_path, contents in files:
                    expand_errors(contents.split('\n'), tcout, file_path)
                lastline = p[i].line if i < len(p) else p[i - 1].line + 9999
                arg0 = p[i0].arg
                assert arg0 is not None
                tc = DataDrivenTestCase(arg0, input, tcout, tcout2, path,
                                        p[i0].line, lastline,
                                        files, output_files, stale_modules,
                                        rechecked_modules, deleted_paths, native_sep)
                out.append(tc)
        if not ok:
            raise ValueError(
                '{}, line {}: Error in test case description'.format(
                    path, p[i0].line))

    return out


class DataDrivenTestCase(ProtoTestCase):
    input = None  # type: List[str]
    output = None  # type: List[str]  # Output for the first pass
    output2 = None  # type: Dict[int, List[str]]  # Output for runs 2+, indexed by run number

    file = ''
    line = 0

    # (file path, file content) tuples
    files = None  # type: List[Tuple[str, str]]
    expected_stale_modules = None  # type: Dict[int, Set[str]]
    expected_rechecked_modules = None  # type: Dict[int, Set[str]]

    # Files/directories to clean up after test case; (is directory, path) tuples
    clean_up = None  # type: List[Tuple[bool, str]]

    def __init__(self,
                 name: str,
                 input: List[str],
                 output: List[str],
                 output2: Dict[int, List[str]],
                 file: str,
                 line: int,
                 lastline: int,
                 files: List[Tuple[str, str]],
                 output_files: List[Tuple[str, str]],
                 expected_stale_modules: Dict[int, Set[str]],
                 expected_rechecked_modules: Dict[int, Set[str]],
                 deleted_paths: Dict[int, Set[str]],
                 native_sep: bool = False,
                 ) -> None:
        super().__init__(name)
        self.input = input
        self.output = output
        self.output2 = output2
        self.lastline = lastline
        self.file = file
        self.line = line
        self.files = files
        self.output_files = output_files
        self.expected_stale_modules = expected_stale_modules
        self.expected_rechecked_modules = expected_rechecked_modules
        self.deleted_paths = deleted_paths
        self.native_sep = native_sep

    def set_up(self) -> None:
        super().set_up()
        encountered_files = set()
        self.clean_up = []
        for paths in self.deleted_paths.values():
            for path in paths:
                self.clean_up.append((False, path))
                encountered_files.add(path)
        for path, content in self.files:
            dir = os.path.dirname(path)
            for d in self.add_dirs(dir):
                self.clean_up.append((True, d))
            with open(path, 'w') as f:
                f.write(content)
            if path not in encountered_files:
                self.clean_up.append((False, path))
                encountered_files.add(path)
            if re.search(r'\.[2-9]$', path):
                # Make sure new files introduced in the second and later runs are accounted for
                renamed_path = path[:-2]
                if renamed_path not in encountered_files:
                    encountered_files.add(renamed_path)
                    self.clean_up.append((False, renamed_path))
        for path, _ in self.output_files:
            # Create directories for expected output and mark them to be cleaned up at the end
            # of the test case.
            dir = os.path.dirname(path)
            for d in self.add_dirs(dir):
                self.clean_up.append((True, d))
            self.clean_up.append((False, path))

    def add_dirs(self, dir: str) -> List[str]:
        """Add all subdirectories required to create dir.

        Return an array of the created directories in the order of creation.
        """
        if dir == '' or os.path.isdir(dir):
            return []
        else:
            dirs = self.add_dirs(os.path.dirname(dir)) + [dir]
            os.mkdir(dir)
            return dirs

    def tear_down(self) -> None:
        # First remove files.
        for is_dir, path in reversed(self.clean_up):
            if not is_dir:
                try:
                    remove(path)
                except FileNotFoundError:
                    # Breaking early using Ctrl+C may happen before file creation. Also, some
                    # files may be deleted by a test case.
                    pass
        # Then remove directories.
        for is_dir, path in reversed(self.clean_up):
            if is_dir:
                pycache = os.path.join(path, '__pycache__')
                if os.path.isdir(pycache):
                    shutil.rmtree(pycache)
                try:
                    rmdir(path)
                except OSError as error:
                    print(' ** Error removing directory %s -- contents:' % path)
                    for item in os.listdir(path):
                        print('  ', item)
                    # Most likely, there are some files in the
                    # directory. Use rmtree to nuke the directory, but
                    # fail the test case anyway, since this seems like
                    # a bug in a test case -- we shouldn't leave
                    # garbage lying around. By nuking the directory,
                    # the next test run hopefully passes.
                    path = error.filename
                    # Be defensive -- only call rmtree if we're sure we aren't removing anything
                    # valuable.
                    if path.startswith(test_temp_dir + '/') and os.path.isdir(path):
                        shutil.rmtree(path)
                    raise
        super().tear_down()

    def find_steps(self) -> List[List[FileOperation]]:
        """Return a list of descriptions of file operations for each incremental step.

        The first list item corresponds to the first incremental step, the second for the
        second step, etc. Each operation can either be a file modification/creation (UpdateFile)
        or deletion (DeleteFile).
        """
        steps = {}  # type: Dict[int, List[FileOperation]]
        for path, _ in self.files:
            m = re.match(r'.*\.([0-9]+)$', path)
            if m:
                num = int(m.group(1))
                assert num >= 2
                target_path = re.sub(r'\.[0-9]+$', '', path)
                module = module_from_path(target_path)
                operation = UpdateFile(module, path, target_path)
                steps.setdefault(num, []).append(operation)
        for num, paths in self.deleted_paths.items():
            assert num >= 2
            for path in paths:
                module = module_from_path(path)
                steps.setdefault(num, []).append(DeleteFile(module, path))
        max_step = max(steps)
        return [steps[num] for num in range(2, max_step + 1)]


def module_from_path(path: str) -> str:
    path = re.sub(r'\.py$', '', path)
    # We can have a mix of Unix-style and Windows-style separators.
    parts = re.split(r'[/\\]', path)
    assert parts[0] == test_temp_dir
    del parts[0]
    module = '.'.join(parts)
    module = re.sub(r'\.__init__$', '', module)
    return module


class TestItem:
    """Parsed test caseitem.

    An item is of the form
      [id arg]
      .. data ..
    """

    id = ''
    arg = ''  # type: Optional[str]

    # Text data, array of 8-bit strings
    data = None  # type: List[str]

    file = ''
    line = 0  # Line number in file

    def __init__(self, id: str, arg: Optional[str], data: List[str], file: str,
                 line: int) -> None:
        self.id = id
        self.arg = arg
        self.data = data
        self.file = file
        self.line = line


def parse_test_data(l: List[str], fnam: str) -> List[TestItem]:
    """Parse a list of lines that represent a sequence of test items."""

    ret = []  # type: List[TestItem]
    data = []  # type: List[str]

    id = None  # type: Optional[str]
    arg = None  # type: Optional[str]

    i = 0
    i0 = 0
    while i < len(l):
        s = l[i].strip()

        if l[i].startswith('[') and s.endswith(']') and not s.startswith('[['):
            if id:
                data = collapse_line_continuation(data)
                data = strip_list(data)
                ret.append(TestItem(id, arg, strip_list(data), fnam, i0 + 1))
            i0 = i
            id = s[1:-1]
            arg = None
            if ' ' in id:
                arg = id[id.index(' ') + 1:]
                id = id[:id.index(' ')]
            data = []
        elif l[i].startswith('[['):
            data.append(l[i][1:])
        elif not l[i].startswith('--'):
            data.append(l[i])
        elif l[i].startswith('----'):
            data.append(l[i][2:])
        i += 1

    # Process the last item.
    if id:
        data = collapse_line_continuation(data)
        data = strip_list(data)
        ret.append(TestItem(id, arg, data, fnam, i0 + 1))

    return ret


def strip_list(l: List[str]) -> List[str]:
    """Return a stripped copy of l.

    Strip whitespace at the end of all lines, and strip all empty
    lines from the end of the array.
    """

    r = []  # type: List[str]
    for s in l:
        # Strip spaces at end of line
        r.append(re.sub(r'\s+$', '', s))

    while len(r) > 0 and r[-1] == '':
        r.pop()

    return r


def collapse_line_continuation(l: List[str]) -> List[str]:
    r = []  # type: List[str]
    cont = False
    for s in l:
        ss = re.sub(r'\\$', '', s)
        if cont:
            r[-1] += re.sub('^ +', '', ss)
        else:
            r.append(ss)
        cont = s.endswith('\\')
    return r


def expand_includes(a: List[str], base_path: str) -> List[str]:
    """Expand @includes within a list of lines.

    Replace all lies starting with @include with the contents of the
    file name following the prefix. Look for the files in base_path.
    """

    res = []  # type: List[str]
    for s in a:
        if s.startswith('@include '):
            fn = s.split(' ', 1)[1].strip()
            with open(os.path.join(base_path, fn)) as f:
                res.extend(f.readlines())
        else:
            res.append(s)
    return res


def expand_variables(s: str) -> str:
    return s.replace('<ROOT>', root_dir)


def expand_errors(input: List[str], output: List[str], fnam: str) -> None:
    """Transform comments such as '# E: message' or
    '# E:3: message' in input.

    The result is lines like 'fnam:line: error: message'.
    """

    for i in range(len(input)):
        # The first in the split things isn't a comment
        for possible_err_comment in input[i].split(' # ')[1:]:
            m = re.search(
                '^([ENW]):((?P<col>\d+):)? (?P<message>.*)$',
                possible_err_comment.strip())
            if m:
                if m.group(1) == 'E':
                    severity = 'error'
                elif m.group(1) == 'N':
                    severity = 'note'
                elif m.group(1) == 'W':
                    severity = 'warning'
                col = m.group('col')
                if col is None:
                    output.append(
                        '{}:{}: {}: {}'.format(fnam, i + 1, severity, m.group('message')))
                else:
                    output.append('{}:{}:{}: {}: {}'.format(
                        fnam, i + 1, col, severity, m.group('message')))


def fix_win_path(line: str) -> str:
    r"""Changes Windows paths to Linux paths in error messages.

    E.g. foo\bar.py -> foo/bar.py.
    """
    line = line.replace(root_dir, root_dir.replace('\\', '/'))
    m = re.match(r'^([\S/]+):(\d+:)?(\s+.*)', line)
    if not m:
        return line
    else:
        filename, lineno, message = m.groups()
        return '{}:{}{}'.format(filename.replace('\\', '/'),
                                lineno or '', message)


def fix_cobertura_filename(line: str) -> str:
    r"""Changes filename paths to Linux paths in Cobertura output files.

    E.g. filename="pkg\subpkg\a.py" -> filename="pkg/subpkg/a.py".
    """
    m = re.search(r'<class .* filename="(?P<filename>.*?)"', line)
    if not m:
        return line
    return '{}{}{}'.format(line[:m.start(1)],
                           m.group('filename').replace('\\', '/'),
                           line[m.end(1):])


##
#
# pytest setup
#
##


# This function name is special to pytest.  See
# http://doc.pytest.org/en/latest/writing_plugins.html#initialization-command-line-and-configuration-hooks
def pytest_addoption(parser: Any) -> None:
    group = parser.getgroup('mypy')
    group.addoption('--update-data', action='store_true', default=False,
                    help='Update test data to reflect actual output'
                         ' (supported only for certain tests)')


# This function name is special to pytest.  See
# http://doc.pytest.org/en/latest/writing_plugins.html#collection-hooks
def pytest_pycollect_makeitem(collector: Any, name: str, obj: Any) -> Any:
    if not isinstance(obj, type) or not issubclass(obj, DataSuite):
        return None
    if obj is DataSuite:
        return None
    return MypyDataSuite(name, parent=collector)


class MypyDataSuite(pytest.Class):  # type: ignore  # inheriting from Any
    def collect(self) -> Iterator['MypyDataCase']:
        cls = self.obj
        for f in cls.files:
            for case in parse_test_cases(os.path.join(test_data_prefix, f),
                                         base_path=cls.base_path,
                                         optional_out=cls.optional_out,
                                         native_sep=cls.native_sep):
                if cls.require_stable and not has_stable_flags(case):
                    continue
                if cls.require_incremental and not is_incremental(case):
                    continue
                yield MypyDataCase(case.name, self, case)


def is_incremental(testcase: DataDrivenTestCase) -> bool:
    return 'incremental' in testcase.name.lower() or 'incremental' in testcase.file


def has_stable_flags(testcase: DataDrivenTestCase) -> bool:
    if any(re.match(r'# flags[2-9]:', line) for line in testcase.input):
        return False
    for filename, contents in testcase.files:
        if os.path.basename(filename).startswith('mypy.ini.'):
            return False
    return True


class MypyDataCase(pytest.Item):  # type: ignore  # inheriting from Any
    def __init__(self, name: str, parent: MypyDataSuite, obj: DataDrivenTestCase) -> None:
        self.skip = False
        if name.endswith('-skip'):
            self.skip = True
            name = name[:-len('-skip')]

        super().__init__(name, parent)
        self.obj = obj

    def runtest(self) -> None:
        if self.skip:
            pytest.skip()
        update_data = self.config.getoption('--update-data', False)
        self.parent.obj(update_data=update_data).run_case(self.obj)

    def setup(self) -> None:
        self.obj.set_up()

    def teardown(self) -> None:
        self.obj.tear_down()

    def reportinfo(self) -> Tuple[str, int, str]:
        return self.obj.file, self.obj.line, self.obj.name

    def repr_failure(self, excinfo: Any) -> str:
        if excinfo.errisinstance(SystemExit):
            # We assume that before doing exit() (which raises SystemExit) we've printed
            # enough context about what happened so that a stack trace is not useful.
            # In particular, uncaught exceptions during semantic analysis or type checking
            # call exit() and they already print out a stack trace.
            excrepr = excinfo.exconly()
        else:
            self.parent._prunetraceback(excinfo)
            excrepr = excinfo.getrepr(style='short')

        return "data: {}:{}:\n{}".format(self.obj.file, self.obj.line, excrepr)


class DataSuite:
    files = None  # type: typing.ClassVar[List[str]]
    base_path = '.'  # type: typing.ClassVar[str]
    optional_out = False  # type: typing.ClassVar[bool]
    native_sep = False  # type: typing.ClassVar[bool]
    require_stable = False  # type: typing.ClassVar[bool]
    require_incremental = False  # type: typing.ClassVar[bool]

    def __init__(self, *, update_data: bool) -> None:
        self.update_data = update_data

    def run_case(self, testcase: DataDrivenTestCase) -> None:
        raise NotImplementedError