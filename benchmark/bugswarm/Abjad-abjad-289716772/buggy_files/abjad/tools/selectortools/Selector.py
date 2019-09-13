import collections
import inspect
from abjad.tools.abctools import AbjadValueObject


class Selector(AbjadValueObject):
    r'''Selector.

    ..  container:: example

        Selects note runs:

        ::

            >>> selector = abjad.select()
            >>> selector = selector.by_leaf()
            >>> selector = selector.by_run(abjad.Note)

        ::


            >>> string = r"c'4 \times 2/3 { d'8 r8 e'8 } r16 f'16 g'8 a'4"
            >>> staff = abjad.Staff(string)
            >>> result = selector(staff)
            >>> selector.color(result)
            >>> abjad.setting(staff).auto_beaming = False
            >>> show(staff) # doctest: +SKIP

        ..  docs::

            >>> f(staff)
            \new Staff \with {
                autoBeaming = ##f
            } {
                \once \override Accidental.color = #red
                \once \override Beam.color = #red
                \once \override Dots.color = #red
                \once \override NoteHead.color = #red
                \once \override Stem.color = #red
                c'4
                \times 2/3 {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                }
                r16
                \once \override Accidental.color = #red
                \once \override Beam.color = #red
                \once \override Dots.color = #red
                \once \override NoteHead.color = #red
                \once \override Stem.color = #red
                f'16
                \once \override Accidental.color = #red
                \once \override Beam.color = #red
                \once \override Dots.color = #red
                \once \override NoteHead.color = #red
                \once \override Stem.color = #red
                g'8
                \once \override Accidental.color = #red
                \once \override Beam.color = #red
                \once \override Dots.color = #red
                \once \override NoteHead.color = #red
                \once \override Stem.color = #red
                a'4
            }

        ::

            >>> selector.print(result)
            Selection([Note("c'4"), Note("d'8")])
            Selection([Note("e'8")])
            Selection([Note("f'16"), Note("g'8"), Note("a'4")])

    ..  container:: example

        Selects first note in each run:

        ::

            >>> selector = abjad.select()
            >>> selector = selector.by_leaf()
            >>> selector = selector.by_run(abjad.Note)
            >>> selector = selector.map(abjad.select()[0])

        ::

            >>> string = r"c'4 \times 2/3 { d'8 r8 e'8 } r16 f'16 g'8 a'4"
            >>> staff = abjad.Staff(string)
            >>> result = selector(staff)
            >>> selector.color(result)
            >>> abjad.setting(staff).auto_beaming = False
            >>> show(staff) # doctest: +SKIP

        ..  docs::

            >>> f(staff)
            \new Staff \with {
                autoBeaming = ##f
            } {
                \once \override Accidental.color = #red
                \once \override Beam.color = #red
                \once \override Dots.color = #red
                \once \override NoteHead.color = #red
                \once \override Stem.color = #red
                c'4
                \times 2/3 {
                    d'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                }
                r16
                \once \override Accidental.color = #red
                \once \override Beam.color = #red
                \once \override Dots.color = #red
                \once \override NoteHead.color = #red
                \once \override Stem.color = #red
                f'16
                g'8
                a'4
            }

        ::

            >>> selector.print(result)
            Note("c'4")
            Note("e'8")
            Note("f'16")

    '''

    ### CLASS VARIABLES ###

    __documentation_section__ = 'Selectors'

    __slots__ = (
        '_callbacks',
        '_template',
        )

    _publish_storage_format = True

    ### INITIALIZER ###

    def __init__(
        self,
        callbacks=None,
        template=None,
        ):
        if callbacks is not None:
            callbacks = tuple(callbacks)
        self._callbacks = callbacks
        if template is not None:
            assert isinstance(template, str), repr(template)
        self._template = template

    ### SPECIAL METHODS ###

    def __call__(self, argument, rotation=None):
        r'''Calls selector callbacks on `argument`.

        Returns selection.
        '''
        import abjad
        if rotation is None:
            rotation = 0
        rotation = int(rotation)
        prototype = (abjad.Component, abjad.Selection, list)
        if not isinstance(argument, prototype):
            raise Exception(argument)
            argument = abjad.select(argument)
        callbacks = self.callbacks or ()
        for callback in callbacks:
            argument = callback(argument, rotation=rotation)
        return argument

    def __getitem__(self, argument):
        r'''Gets item or slice identified by `argument`.

        Returns another selector.
        '''
        import abjad
        if isinstance(argument, slice):
            callback = abjad.GetSliceCallback(
                start=argument.start,
                stop=argument.stop,
                )
        elif isinstance(argument, int):
            callback = abjad.GetItemCallback(
                item=argument,
                )
        else:
            raise ValueError(argument)
        return self._append_callback(callback)

    ### PRIVATE METHODS ###

    def _append_callback(self, callback):
        callbacks = self.callbacks or ()
        callbacks = callbacks + (callback,)
        return type(self)(callbacks)

    def _get_format_specification(self):
        import abjad
        if self.template is None:
            return super(Selector, self)._get_format_specification()
        return abjad.FormatSpecification(
            client=self,
            repr_is_indented=False,
            storage_format_is_indented=False,
            storage_format_args_values=[self.template],
            storage_format_forced_override=self.template,
            storage_format_kwargs_names=(),
            )

    def _get_template(self, frame):
        import abjad
        try:
            frame_info = inspect.getframeinfo(frame)
            function_name = frame_info.function
            arguments = abjad.Expression._wrap_arguments(frame)
            stem = self.template or 'abjad.select()'
            template = f'{stem}.{function_name}({arguments})'
        finally:
            del frame
        return template

    ### PUBLIC METHODS ###

    def append_callback(self, callback):
        r'''Appends `callback` to selector.

        Composers can create their own selector callback classes with
        specialized composition-specific logic. `Selector.append_callback()`
        allows composers to use those composition-specific selector callbacks
        in the component selector pipeline.

        ..  container:: example

            A custom selector callback class can be created to only select
            chords containing the pitch-classes C, E and G. A selector can then
            be configured with that custom callback via `append_callback()`:

            ::

                >>> from abjad.tools import abctools
                >>> class CMajorSelectorCallback(abctools.AbjadValueObject):
                ...     def __call__(self, argument, rotation=None):
                ...         c_major_pcs = abjad.PitchClassSet("c e g")
                ...         result = []
                ...         for item in argument:
                ...             if not isinstance(item, abjad.Chord):
                ...                 continue
                ...             pitches = item.written_pitches
                ...             pcs = abjad.PitchClassSet(pitches)
                ...             if pcs == c_major_pcs:
                ...                 result.append(item)
                ...         return abjad.Selection(result)

            ::

                >>> staff = abjad.Staff("<g' d'>4 <c' e' g'>4 r4 <e' g' c''>2 fs,4")
                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.append_callback(CMajorSelectorCallback())

            ::

                >>> selector(staff)
                Selection([Chord("<c' e' g'>4"), Chord("<e' g' c''>2")])

        Returns new expression.
        '''
        return self._append_callback(callback)

    def by_class(self, prototype=None):
        r'''Selects by class.

        ..  container:: example

            Selects notes:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(abjad.Note)

            ::

                >>> staff = abjad.Staff("c'4 d'8 ~ d'16 e'16 ~ e'8 r4 g'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'4
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'16 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    r4
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    g'8
                }

            ::

                >>> selector.print(result)
                Note("c'4")
                Note("d'8")
                Note("d'16")
                Note("e'16")
                Note("e'8")
                Note("g'8")

        Returns new expression.
        '''
        import abjad
        callback = abjad.ByClassCallback(prototype=prototype)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def by_contiguity(self):
        r'''Selects by contiguity.

        ..  container:: example

            Selects contiguous groups of sixteenth notes:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_duration('==', (1, 16))
                >>> selector = selector.by_contiguity()

            ::

                >>> staff = abjad.Staff("c'4 d'16 d' d' d' e'4 f'16 f' f' f'")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff {
                    c'4
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'16
                    e'4
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'16
                }

            ::

                >>> selector.print(result)
                Selection([Note("d'16"), Note("d'16"), Note("d'16"), Note("d'16")])
                Selection([Note("f'16"), Note("f'16"), Note("f'16"), Note("f'16")])

        ..  container:: example

            Selects the first leaf in each contiguous group of short-duration
            logical ties:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie()
                >>> selector = selector.by_duration('<', (1, 4))
                >>> selector = selector.by_contiguity()
                >>> selector = selector.map(abjad.select().by_leaf()[0])

            ::

                >>> staff = abjad.Staff("c'4 d'8 ~ d'16 e'16 ~ e'8 f'4 g'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'4
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8 ~
                    d'16
                    e'16 ~
                    e'8
                    f'4
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    g'8
                }

            ::

                >>> selector.print(result)
                Note("d'8")
                Note("g'8")

        Returns new expression.
        '''
        import abjad
        callback = abjad.ByContiguityCallback()
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def by_duration(self, inequality=None, duration=None, preprolated=None):
        r'''Selects by duration.

        ..  container:: example

            Selects note runs with duration equal to 2/8:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_run(abjad.Note)
                >>> selector = selector.by_duration(abjad.Duration(2, 8))

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    r8
                    f'8
                    g'8
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("d'8"), Note("e'8")])

        ..  container:: example

            Selects note runs with duration less than 3/8:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_run(abjad.Note)
                >>> selector = selector.by_duration('<', abjad.Duration(3, 8))

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    r8
                    f'8
                    g'8
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8")])
                Selection([Note("d'8"), Note("e'8")])

        ..  container:: example

            Selects note runs with duration greater than or equal to 1/4:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_run(abjad.Note)
                >>> selector = selector.by_duration('>=', abjad.Duration(1, 4))

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    g'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("d'8"), Note("e'8")])
                Selection([Note("f'8"), Note("g'8"), Note("a'8")])

        ..  container:: example

            Selects logical ties with preprolated duration equal to 1/8:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie()
                >>> selector = selector.by_duration(
                ...     '==',
                ...     abjad.Duration(1, 8),
                ...     preprolated=True,
                ...     )

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 3/4 { c'16 d'16 ~ d'16 e'16 ~ }
                ...     {e'16 f'16 ~ f'16 g'16 ~ }
                ...     \times 5/4 { g'16 a'16 ~ a'16 b'16 }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP


            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \tweak text #tuplet-number::calc-fraction-text
                    \times 3/4 {
                        c'16
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'16 ~
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'16
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'16 ~
                    }
                    {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'16
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        f'16 ~
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        f'16
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        g'16 ~
                    }
                    \tweak text #tuplet-number::calc-fraction-text
                    \times 5/4 {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        g'16
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        a'16 ~
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        a'16
                        b'16
                    }
                }

            ::

                >>> selector.print(result)
                LogicalTie([Note("d'16"), Note("d'16")])
                LogicalTie([Note("e'16"), Note("e'16")])
                LogicalTie([Note("f'16"), Note("f'16")])
                LogicalTie([Note("g'16"), Note("g'16")])
                LogicalTie([Note("a'16"), Note("a'16")])

        Returns new expression.
        '''
        import abjad
        duration_expr = None
        prototype = (abjad.Duration, abjad.DurationInequality)
        if isinstance(inequality, prototype):
            duration_expr = inequality
        elif isinstance(inequality, str) and duration is not None:
            duration_expr = abjad.DurationInequality(
                duration=duration,
                operator_string=inequality,
                )
        elif inequality is None and duration is not None:
            duration_expr = abjad.DurationInequality(
                duration=duration,
                operator_string='==',
                )
        if not isinstance(duration_expr, prototype):
            raise ValueError(inequality, duration)
        callback = abjad.ByDurationCallback(
            duration=duration_expr,
            preprolated=preprolated,
            )
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def by_leaf(
        self,
        prototype=None,
        head=None,
        pitched=None,
        tail=None,
        trim=None,
        ):
        r'''Selects by leaf.

        ..  container:: example

            Selects leaves:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { r8 d' e' } f' r
                ...     r f' \times 2/3 { e' d' r8 }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        \once \override Dots.color = #red
                        \once \override Rest.color = #red
                        r8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                    }
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Dots.color = #blue
                        \once \override Rest.color = #blue
                        r8
                    }
                }

            ::

                >>> selector.print(result)
                Rest('r8')
                Note("d'8")
                Note("e'8")
                Note("f'8")
                Rest('r8')
                Rest('r8')
                Note("f'8")
                Note("e'8")
                Note("d'8")
                Rest('r8')

        ..  container:: example

            Selects pitched leaves:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf(pitched=True)
                >>> selection = selector(staff)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { r8 d' e' } f' r
                ...     r f' \times 2/3 { e' d' r8 }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        r8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                    }
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                    r8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        r8
                    }
                }

            ::

                >>> selector.print(result)
                Note("d'8")
                Note("e'8")
                Note("f'8")
                Note("f'8")
                Note("e'8")
                Note("d'8")

        ..  container:: example

            Selects trimmed leaves:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf(trim=True)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { r8 d' e' } f' r
                ...     r f' \times 2/3 { e' d' r8 }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        r8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                    }
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        r8
                    }
                }

            ::

                >>> selector.print(result)
                Note("d'8")
                Note("e'8")
                Note("f'8")
                Rest('r8')
                Rest('r8')
                Note("f'8")
                Note("e'8")
                Note("d'8")

            Use trimmed leaves with ottava spanners:

            ::

                >>> abjad.attach(abjad.OctavationSpanner(), result)
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        r8
                        \ottava #1
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                    }
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        \ottava #0
                        r8
                    }
                }

            Regression: selects trimmed leaves (even when there are no rests to
            trim):

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf(trim=True)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { c'8 d' e' } f' r
                ...     r f' \times 2/3 { e' d' c' }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        c'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                    }
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        c'8
                    }
                }

            ::

                >>> selector.print(result)
                Note("c'8")
                Note("d'8")
                Note("e'8")
                Note("f'8")
                Rest('r8')
                Rest('r8')
                Note("f'8")
                Note("e'8")
                Note("d'8")
                Note("c'8")

        ..  container:: example

            Selects leaves in tuplets:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(abjad.Tuplet)
                >>> selector = selector.by_leaf()

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { r8 d' e' } f' r
                ...     r f' \times 2/3 { e' d' r8 }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        \once \override Dots.color = #red
                        \once \override Rest.color = #red
                        r8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                    }
                    f'8
                    r8
                    r8
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Dots.color = #blue
                        \once \override Rest.color = #blue
                        r8
                    }
                }

            ::

                >>> selector.print(result)
                Rest('r8')
                Note("d'8")
                Note("e'8")
                Note("e'8")
                Note("d'8")
                Rest('r8')

            Selects trimmed leaves in tuplets:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(abjad.Tuplet)
                >>> selector = selector.by_leaf(trim=True)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { r8 d' e' } f' r
                ...     r f' \times 2/3 { e' d' r8 }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        r8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                    }
                    f'8
                    r8
                    r8
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        r8
                    }
                }

            ::

                >>> selector.print(result)
                Note("d'8")
                Note("e'8")
                Note("e'8")
                Note("d'8")

        ..  container:: example

            Selects pitched heads in tuplets.

            This is the correct selection for most articulations:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(abjad.Tuplet)
                >>> selector = selector.by_leaf(head=True, pitched=True)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { c'8 d' ~ d' } e' r
                ...     r e' \times 2/3 { d' ~ d' c' }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        c'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8 ~
                        d'8
                    }
                    e'8
                    r8
                    r8
                    e'8
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8 ~
                        d'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        c'8
                    }
                }

            ::

                >>> selector.print(result)
                Note("c'8")
                Note("d'8")
                Note("d'8")
                Note("c'8")

        ..  container:: example

            Selects pitched tails in tuplets.

            This is the correct selection for laissez vibrer:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(abjad.Tuplet)
                >>> selector = selector.by_leaf(tail=True, pitched=True)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { c'8 d' ~ d' } e' r
                ...     r e' \times 2/3 { d' ~ d' c' }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        c'8
                        d'8 ~
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                    }
                    e'8
                    r8
                    r8
                    e'8
                    \times 2/3 {
                        d'8 ~
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        c'8
                    }
                }

            ::

                >>> selector.print(result)
                Note("c'8")
                Note("d'8")
                Note("d'8")
                Note("c'8")

        ..  container:: example

            Selects chord heads in tuplets.

            This is the correct selection for arpeggios:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(abjad.Tuplet)
                >>> selector = selector.by_leaf(abjad.Chord, head=True)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { <c' e' g'>8 ~ <c' e' g'> d' } e' r
                ...     r <g d' fs'> \times 2/3 { e' <c' d'> ~ <c' d'> }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        <c' e' g'>8 ~
                        <c' e' g'>8
                        d'8
                    }
                    e'8
                    r8
                    r8
                    <g d' fs'>8
                    \times 2/3 {
                        e'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        <c' d'>8 ~
                        <c' d'>8
                    }
                }

            ::

                >>> selector.print(result)
                Chord("<c' e' g'>8")
                Chord("<c' d'>8")

        Returns new expression.
        '''
        import abjad
        if pitched:
            prototype = (abjad.Chord, abjad.Note)
        elif prototype is None:
            prototype = abjad.Leaf
        callback = abjad.ByClassCallback(
            prototype=prototype,
            head=head,
            tail=tail,
            trim=trim,
            )
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def by_length(self, inequality=None, length=None):
        r'''Selects by length.

        ..  container:: example

            Selects notes runs with length greater than 1:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_run(abjad.Note)
                >>> selector = selector.by_length('>', 1)

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    g'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("d'8"), Note("e'8")])
                Selection([Note("f'8"), Note("g'8"), Note("a'8")])

        ..  container:: example

            Selects note runs with length less than 3:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_run(abjad.Note)
                >>> selector = selector.by_length('<', 3)

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    r8
                    f'8
                    g'8
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8")])
                Selection([Note("d'8"), Note("e'8")])

        Returns new expression.
        '''
        import abjad
        length_expr = None
        if isinstance(inequality, ( int, float, abjad.LengthInequality)):
            length_expr = inequality
        elif isinstance(inequality, str) and length is not None:
            length_expr = abjad.LengthInequality(
                length=int(length),
                operator_string=inequality,
                )
        elif inequality is None and length is not None:
            length_expr = abjad.LengthInequality(
                length=int(length),
                operator_string='==',
                )
        if not isinstance(length_expr, ( int, float, abjad.LengthInequality)):
            raise ValueError(inequality, length)
        callback = abjad.ByLengthCallback(length=length_expr)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def by_logical_measure(self):
        r'''Selects by logical measure.

        Returns new expression.
        '''
        import abjad
        callback = abjad.ByLogicalMeasureCallback()
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def by_logical_tie(
        self,
        pitched=False,
        trivial=True,
        with_grace_notes=False,
        ):
        r'''Selects by logical tie.

        ..  container:: example

            Selects logical ties:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie()

            ::

                >>> staff = abjad.Staff("c'8 d' ~ { d' e' r f'~ } f' r")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8 ~
                    {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                        \once \override Dots.color = #blue
                        \once \override Rest.color = #blue
                        r8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        f'8 ~
                    }
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                }

            ::

                >>> selector.print(result)
                LogicalTie([Note("c'8")])
                LogicalTie([Note("d'8"), Note("d'8")])
                LogicalTie([Note("e'8")])
                LogicalTie([Rest('r8')])
                LogicalTie([Note("f'8"), Note("f'8")])
                LogicalTie([Rest('r8')])

        ..  container:: example

            Selects pitched logical ties:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)

            ::

                >>> staff = abjad.Staff("c'8 d' ~ { d' e' r f'~ } f' r")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8 ~
                    {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                        r8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        f'8 ~
                    }
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    r8
                }

            ::

                >>> selector.print(result)
                LogicalTie([Note("c'8")])
                LogicalTie([Note("d'8"), Note("d'8")])
                LogicalTie([Note("e'8")])
                LogicalTie([Note("f'8"), Note("f'8")])

        ..  container:: example

            Selects pitched nontrivial logical ties:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(
                ...     pitched=True,
                ...     trivial=False,
                ...     )

            ::

                >>> staff = abjad.Staff("c'8 d' ~ { d' e' r f'~ } f' r")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8 ~
                    {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        e'8
                        r8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        f'8 ~
                    }
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    r8
                }

            ::

                >>> selector.print(result)
                LogicalTie([Note("d'8"), Note("d'8")])
                LogicalTie([Note("f'8"), Note("f'8")])

        ..  container:: example

            Selects pitched logical ties (starting) in each tuplet:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(prototype=abjad.Tuplet)
                >>> get = abjad.select().by_logical_tie(pitched=True)
                >>> selector = selector.map(get)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { c'8 d' e'  ~ } e' f' ~
                ...     \times 2/3 { f' g' a' ~ } a' b' ~
                ...     \times 2/3 { b' c'' d'' }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        c'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8 ~
                    }
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    f'8 ~
                    \times 2/3 {
                        f'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        g'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        a'8 ~
                    }
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    a'8
                    b'8 ~
                    \times 2/3 {
                        b'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        c''8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d''8
                    }
                }

            ::

                >>> selector.print(result)
                [LogicalTie([Note("c'8")]), LogicalTie([Note("d'8")]), LogicalTie([Note("e'8"), Note("e'8")])]
                [LogicalTie([Note("g'8")]), LogicalTie([Note("a'8"), Note("a'8")])]
                [LogicalTie([Note("c''8")]), LogicalTie([Note("d''8")])]

        ..  container:: example

            Selects pitched logical ties (starting) in each of the last two
            tuplets:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(prototype=abjad.Tuplet)
                >>> selector = selector.get_slice(start=-2)
                >>> get = abjad.select().by_logical_tie(pitched=True)
                >>> selector = selector.map(get)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { c'8 d' e'  ~ } e' f' ~
                ...     \times 2/3 { f' g' a' ~ } a' b' ~
                ...     \times 2/3 { b' c'' d'' }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        c'8
                        d'8
                        e'8 ~
                    }
                    e'8
                    f'8 ~
                    \times 2/3 {
                        f'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        g'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        a'8 ~
                    }
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    a'8
                    b'8 ~
                    \times 2/3 {
                        b'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        c''8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d''8
                    }
                }

            ::

                >>> selector.print(result)
                [LogicalTie([Note("g'8")]), LogicalTie([Note("a'8"), Note("a'8")])]
                [LogicalTie([Note("c''8")]), LogicalTie([Note("d''8")])]

        Returns new expression.
        '''
        import abjad
        callback = abjad.ByLogicalTieCallback(
            pitched=pitched,
            trivial=trivial,
            with_grace_notes=with_grace_notes,
            )
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def by_pattern(self, pattern=None):
        r'''Selects by `pattern`.

        ..  container:: example

            Selects every other leaf:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_pattern(
                ...     pattern=abjad.index_every([0], period=2),
                ...     )

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ::

                >>> selector.print(result)
                Note("c'8")
                Note("d'8")
                Note("e'8")
                Rest('r8')

        ..  container:: example

            Selects every other leaf rotated one to the right:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_pattern(
                ...     pattern=abjad.index_every([0], period=2),
                ...     )

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff, rotation=1)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8 ~
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8 ~
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                }

            ::

                >>> selector.print(result)
                Note("d'8")
                Note("e'8")
                Note("e'8")
                Note("f'8")

        ..  container:: example

            Selects every other logical tie:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.by_pattern(
                ...     pattern=abjad.index_every([0], period=2),
                ...     )

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    d'8 ~
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    r8
                    f'8
                }

            ::

                >>> selector.print(result)
                LogicalTie([Note("c'8")])
                LogicalTie([Note("e'8"), Note("e'8"), Note("e'8")])

        ..  container:: example

            Selects note 1 in each pitched logical tie:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> get = abjad.select().by_pattern(abjad.index([1]))
                >>> selector = selector.map(get)

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    d'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8 ~
                    e'8
                    r8
                    f'8
                }

            ::

                >>> selector.print(result)
                Selection(music=())
                Selection([Note("d'8")])
                Selection([Note("e'8")])
                Selection(music=())

        Returns new expression.
        '''
        import abjad
        callback = abjad.ByPatternCallback(pattern=pattern)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    # TODO: implement pitch-inequality class.
    def by_pitch(self, pitches=None):
        r'''Selects by pitch.

        ..  container:: example

            Selects leaves with C4:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_pitch(pitches="c'")

            ::

                >>> staff = abjad.Staff("c'8 d'8 ~ d'8 e'8")
                >>> staff.extend("r8 <c' e' g'>8 ~ <c' e' g'>4")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    d'8 ~
                    d'8
                    e'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    <c' e' g'>8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    <c' e' g'>4
                }

            ::

                >>> selector.print(result)
                Note("c'8")
                Chord("<c' e' g'>8")
                Chord("<c' e' g'>4")

        ..  container:: example

            Selects leaves with C4 or E4:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_pitch(pitches="c' e'")

            ::

                >>> staff = abjad.Staff("c'8 d'8 ~ d'8 e'8")
                >>> staff.extend("r8 <c' e' g'>8 ~ <c' e' g'>4")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    d'8 ~
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    <c' e' g'>8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    <c' e' g'>4
                }

            ::

                >>> selector.print(result)
                Note("c'8")
                Note("e'8")
                Chord("<c' e' g'>8")
                Chord("<c' e' g'>4")

        ..  container:: example

            Selects logical ties with C4:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie()
                >>> selector = selector.by_pitch(pitches=abjad.NamedPitch('C4'))

            ::

                >>> staff = abjad.Staff("c'8 d'8 ~ d'8 e'8")
                >>> staff.extend("r8 <c' e' g'>8 ~ <c' e' g'>4")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    d'8 ~
                    d'8
                    e'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    <c' e' g'>8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    <c' e' g'>4
                }

            ::

                >>> selector.print(result)
                LogicalTie([Note("c'8")])
                LogicalTie([Chord("<c' e' g'>8"), Chord("<c' e' g'>4")])

        Returns new expression.
        '''
        import abjad
        callback = abjad.ByPitchCallback(pitches=pitches)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def by_run(
        self,
        prototype=None,
        ):
        r'''Selects by run.

        ..  container:: example

            Selects pitched runs:

            ::

                >>> prototype = (abjad.Chord, abjad.Note)
                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_run(prototype=prototype)

            ::

                >>> string = r"c'8 d' r \times 2/3 { e' r f' } g' a' r"
                >>> staff = abjad.Staff(string)
                >>> staff.extend("r8 <c' e' g'>8 ~ <c' e' g'>4")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    r8
                    \times 2/3 {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                        r8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        f'8
                    }
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    g'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    a'8
                    r8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    <c' e' g'>8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    <c' e' g'>4
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Note("d'8")])
                Selection([Note("e'8")])
                Selection([Note("f'8"), Note("g'8"), Note("a'8")])
                Selection([Chord("<c' e' g'>8"), Chord("<c' e' g'>4")])

        Returns new expression.
        '''
        import abjad
        callback = abjad.ByRunCallback(prototype)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def color(self, result, colors=None):
        r'''Colors `result`.

        Returns none.
        '''
        import abjad
        abjad.label(result).color_selections(self, colors=colors)

    def flatten(self, depth=-1):
        r'''Flattens selection.

        ..  container:: example

            Flattens middle pitched logical ties:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector[1:-1]
                >>> selector = selector.flatten()

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    r8
                    f'8
                }

            ::

                >>> selector.print(result)
                Note("d'8")
                Note("d'8")
                Note("e'8")
                Note("e'8")
                Note("e'8")

        Returns new expression.
        '''
        import abjad
        callback = abjad.FlattenCallback(depth=depth)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def get_item(self, n):
        r'''Gets item `n`.

        ..  container:: example

            Gets leaf 1:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.get_item(1)

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    \once \override Accidental.color = #green
                    \once \override Beam.color = #green
                    \once \override Dots.color = #green
                    \once \override NoteHead.color = #green
                    \once \override Stem.color = #green
                    d'8 ~
                    d'8
                    e'8 ~
                    e'8 ~
                    e'8
                    r8
                    f'8
                }

            ::

                >>> selector.print(result)
                Note("d'8")

        ..  container:: example

            Gets logical tie 1:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.get_item(1)

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    \once \override Accidental.color = #green
                    \once \override Beam.color = #green
                    \once \override Dots.color = #green
                    \once \override NoteHead.color = #green
                    \once \override Stem.color = #green
                    d'8 ~
                    \once \override Accidental.color = #green
                    \once \override Beam.color = #green
                    \once \override Dots.color = #green
                    \once \override NoteHead.color = #green
                    \once \override Stem.color = #green
                    d'8
                    e'8 ~
                    e'8 ~
                    e'8
                    r8
                    f'8
                }

            ::

                >>> selector.print(result)
                LogicalTie([Note("d'8"), Note("d'8")])

        ..  container:: example

            Gets tuplet 1:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(prototype=abjad.Tuplet)
                >>> selector = selector.flatten()
                >>> selector = selector.get_item(n=1)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { r8 d' e' } f' r
                ...     r f' \times 2/3 { e' d' r8 }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        r8
                        d'8
                        e'8
                    }
                    f'8
                    r8
                    r8
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #green
                        \once \override Beam.color = #green
                        \once \override Dots.color = #green
                        \once \override NoteHead.color = #green
                        \once \override Stem.color = #green
                        e'8
                        \once \override Accidental.color = #green
                        \once \override Beam.color = #green
                        \once \override Dots.color = #green
                        \once \override NoteHead.color = #green
                        \once \override Stem.color = #green
                        d'8
                        \once \override Dots.color = #green
                        \once \override Rest.color = #green
                        r8
                    }
                }

            ::

                >>> selector.print(result)
                Tuplet(Multiplier(2, 3), "e'8 d'8 r8")

        ..  container:: example

            Gets the first leaf in each pitched logical tie:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.map(abjad.select().by_leaf()[0])

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8 ~
                    d'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8 ~
                    e'8 ~
                    e'8
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                }

            ::

                >>> selector.print(result)
                Note("c'8")
                Note("d'8")
                Note("e'8")
                Note("f'8")


        Returns new expression.
        '''
        import abjad
        callback = abjad.GetItemCallback(item=n)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def get_slice(self, start=None, stop=None):
        r'''Gets slice.

        ..  container:: example

            Gets nonlast pitched logical ties:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.get_slice(stop=-1)

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    r8
                    f'8
                }

            ::

                >>> selector.print(result)
                LogicalTie([Note("c'8")])
                LogicalTie([Note("d'8"), Note("d'8")])
                LogicalTie([Note("e'8"), Note("e'8"), Note("e'8")])

            Gets nonfirst pitched logical ties:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.get_slice(start=1)

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                }

            ::

                >>> selector.print(result)
                LogicalTie([Note("d'8"), Note("d'8")])
                LogicalTie([Note("e'8"), Note("e'8"), Note("e'8")])
                LogicalTie([Note("f'8")])

        ..  container:: example

            Gets nonfirst leaves in each pitched logical tie:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.map(abjad.select()[1:])

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    d'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    r8
                    f'8
                }

            ::

                >>> selector.print(result)
                Selection(music=())
                Selection([Note("d'8")])
                Selection([Note("e'8"), Note("e'8")])
                Selection(music=())

            Gets nonlast leaves in each pitched logical tie:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.map(abjad.select()[:-1])

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8 ~
                    d'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8 ~
                    e'8
                    r8
                    f'8
                }

            ::

                >>> selector.print(result)
                Selection(music=())
                Selection([Note("d'8")])
                Selection([Note("e'8"), Note("e'8")])
                Selection(music=())

        ..  container:: example

            Gets last three leaves:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector[-3:]

            ::

                >>> staff = abjad.Staff(r"c'8 d'8 ~ d'8 e'8 ~ e'8 ~ e'8 r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    c'8
                    d'8 ~
                    d'8
                    e'8 ~
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                }

            ::

                >>> selector.print(result)
                Note("e'8")
                Rest('r8')
                Note("f'8")

        Returns new expression.
        '''
        import abjad
        callback = abjad.GetSliceCallback(start=start, stop=stop)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def group_by_pitch(self, allow_discontiguity=False):
        r'''Groups by pitch.

        ..  container:: example

            Groups pitched leaves by pitch:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf(pitched=True)
                >>> selector = selector.group_by_pitch()

            ::

                >>> string = r"c'8 ~ c'16 c'16 r8 c'16 c'16"
                >>> staff = abjad.Staff(r"""
                ...     c'8 ~ c'16 c'16 r8 c'16 c'16
                ...     d'8 ~ d'16 d'16 r8 d'16 d'16
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    c'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    c'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'16
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Note("c'16"), Note("c'16")])
                Selection([Note("c'16"), Note("c'16")])
                Selection([Note("d'8"), Note("d'16"), Note("d'16")])
                Selection([Note("d'16"), Note("d'16")])

            Groups discontiguous pitched leaves by pitch:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf(pitched=True)
                >>> selector = selector.group_by_pitch(
                ...     allow_discontiguity=True,
                ...     )

            ::

                >>> string = r"c'8 ~ c'16 c'16 r8 c'16 c'16"
                >>> staff = abjad.Staff(r"""
                ...     c'8 ~ c'16 c'16 r8 c'16 c'16
                ...     d'8 ~ d'16 d'16 r8 d'16 d'16
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Note("c'16"), Note("c'16"), Note("c'16"), Note("c'16")])
                Selection([Note("d'8"), Note("d'16"), Note("d'16"), Note("d'16"), Note("d'16")])

        ..  container:: example

            Groups pitched logical ties by pitch:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.group_by_pitch()

            ::

                >>> staff = abjad.Staff(r"""
                ...     c'8 ~ c'16 c'16 r8 c'16 c'16
                ...     d'8 ~ d'16 d'16 r8 d'16 d'16
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    c'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    c'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'16
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                }

            ::

                >>> selector.print(result)
                Selection([LogicalTie([Note("c'8"), Note("c'16")]), LogicalTie([Note("c'16")])])
                Selection([LogicalTie([Note("c'16")]), LogicalTie([Note("c'16")])])
                Selection([LogicalTie([Note("d'8"), Note("d'16")]), LogicalTie([Note("d'16")])])
                Selection([LogicalTie([Note("d'16")]), LogicalTie([Note("d'16")])])

            Groups discontiguous pitched logical ties by pitch:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.group_by_pitch(
                ...     allow_discontiguity=True,
                ...     )

            ::

                >>> staff = abjad.Staff(r"""
                ...     c'8 ~ c'16 c'16 r8 c'16 c'16
                ...     d'8 ~ d'16 d'16 r8 d'16 d'16
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'16
                }

            ::

                >>> selector.print(result)
                Selection([LogicalTie([Note("c'8"), Note("c'16")]), LogicalTie([Note("c'16")]), LogicalTie([Note("c'16")]), LogicalTie([Note("c'16")])])
                Selection([LogicalTie([Note("d'8"), Note("d'16")]), LogicalTie([Note("d'16")]), LogicalTie([Note("d'16")]), LogicalTie([Note("d'16")])])

        Returns new expression.
        '''
        import abjad
        callback = abjad.GroupByPitchCallback(
            allow_discontiguity=allow_discontiguity,
            )
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def map(self, callback):
        r'''Maps `callback` to selector.

        Returns new expression.
        '''
        import abjad
        callback = abjad.MapCallback(callback=callback)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def partition_by_counts(
        self,
        counts,
        cyclic=False,
        fuse_overhang=False,
        nonempty=False,
        overhang=False,
        rotate=False,
        ):
        r'''Partitions by counts.

        ..  container:: example

            Partitions once by counts without overhang:

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.partition_by_counts(
                ...     [3],
                ...     cyclic=False,
                ...     overhang=False,
                ...     )


            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    e'8
                    r8
                    f'8
                    g'8
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Rest('r8'), Note("d'8")])

        ..  container:: example

            Partitions cyclically by counts without overhang:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.partition_by_counts(
                ...     [3],
                ...     cyclic=True,
                ...     overhang=False,
                ...     )

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    g'8
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Rest('r8'), Note("d'8")])
                Selection([Note("e'8"), Rest('r8'), Note("f'8")])

        ..  container:: example

            Partitions cyclically by counts with overhang:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.partition_by_counts(
                ...     [3],
                ...     cyclic=True,
                ...     overhang=True,
                ...     )

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    g'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Rest('r8'), Note("d'8")])
                Selection([Note("e'8"), Rest('r8'), Note("f'8")])
                Selection([Note("g'8"), Note("a'8")])

        ..  container:: example

            Partitions cyclically by counts with fused overhang:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.partition_by_counts(
                ...     [3],
                ...     cyclic=True,
                ...     fuse_overhang=True,
                ...     overhang=True,
                ...     )

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    g'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Rest('r8'), Note("d'8")])
                Selection([Note("e'8"), Rest('r8'), Note("f'8"), Note("g'8"), Note("a'8")])

        ..  container:: example

            Partitions cyclically by counts with overhang:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.partition_by_counts(
                ...     [1, 2, 3],
                ...     cyclic=True,
                ...     overhang=True,
                ...     )

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8 b'8 r8 c''8")
                >>> result = selector(staff)
                >>> selector.color(result, ['red', 'blue', 'cyan'])
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #cyan
                    \once \override Beam.color = #cyan
                    \once \override Dots.color = #cyan
                    \once \override NoteHead.color = #cyan
                    \once \override Stem.color = #cyan
                    e'8
                    \once \override Dots.color = #cyan
                    \once \override Rest.color = #cyan
                    r8
                    \once \override Accidental.color = #cyan
                    \once \override Beam.color = #cyan
                    \once \override Dots.color = #cyan
                    \once \override NoteHead.color = #cyan
                    \once \override Stem.color = #cyan
                    f'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    g'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    a'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    b'8
                    \once \override Dots.color = #cyan
                    \once \override Rest.color = #cyan
                    r8
                    \once \override Accidental.color = #cyan
                    \once \override Beam.color = #cyan
                    \once \override Dots.color = #cyan
                    \once \override NoteHead.color = #cyan
                    \once \override Stem.color = #cyan
                    c''8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8")])
                Selection([Rest('r8'), Note("d'8")])
                Selection([Note("e'8"), Rest('r8'), Note("f'8")])
                Selection([Note("g'8")])
                Selection([Note("a'8"), Note("b'8")])
                Selection([Rest('r8'), Note("c''8")])

        ..  container:: example

            Partitions cyclically by counts rotated one to the left, with
            overhang:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.partition_by_counts(
                ...     [1, 2, 3],
                ...     cyclic=True,
                ...     overhang=True,
                ...     rotate=True,
                ...     )


            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8 b'8 r8 c''8")
                >>> result = selector(staff, rotation=1)
                >>> selector.color(result, ['red', 'blue', 'cyan'])
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #cyan
                    \once \override Beam.color = #cyan
                    \once \override Dots.color = #cyan
                    \once \override NoteHead.color = #cyan
                    \once \override Stem.color = #cyan
                    f'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    g'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    a'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    b'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    c''8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Rest('r8')])
                Selection([Note("d'8"), Note("e'8"), Rest('r8')])
                Selection([Note("f'8")])
                Selection([Note("g'8"), Note("a'8")])
                Selection([Note("b'8"), Rest('r8'), Note("c''8")])

        Returns new expression.
        '''
        import abjad
        callback = abjad.PartitionByCountsCallback(
            counts,
            cyclic=cyclic,
            fuse_overhang=fuse_overhang,
            nonempty=nonempty,
            overhang=overhang,
            rotate=rotate,
            )
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def partition_by_ratio(self, ratio):
        r'''Partitions by ratio.

        ..  container:: example

            Partitions leaves by ratio of 1:1:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.partition_by_ratio((1, 1))

            ::

                >>> staff = abjad.Staff(r"c'8 d' r \times 2/3 { e' r f' } g' a' r")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \times 2/3 {
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                        \once \override Dots.color = #red
                        \once \override Rest.color = #red
                        r8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        f'8
                    }
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    g'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    a'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Note("d'8"), Rest('r8'), Note("e'8"), Rest('r8')])
                Selection([Note("f'8"), Note("g'8"), Note("a'8"), Rest('r8')])

        ..  container:: example

            Partitions leaves by ratio of 1:1:1:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.partition_by_ratio((1, 1, 1))

            ::

                >>> staff = abjad.Staff(r"c'8 d' r \times 2/3 { e' r f' } g' a' r")
                >>> result = selector(staff)
                >>> selector.color(result, ['red', 'blue', 'cyan'])
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \times 2/3 {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                        \once \override Dots.color = #blue
                        \once \override Rest.color = #blue
                        r8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        f'8
                    }
                    \once \override Accidental.color = #cyan
                    \once \override Beam.color = #cyan
                    \once \override Dots.color = #cyan
                    \once \override NoteHead.color = #cyan
                    \once \override Stem.color = #cyan
                    g'8
                    \once \override Accidental.color = #cyan
                    \once \override Beam.color = #cyan
                    \once \override Dots.color = #cyan
                    \once \override NoteHead.color = #cyan
                    \once \override Stem.color = #cyan
                    a'8
                    \once \override Dots.color = #cyan
                    \once \override Rest.color = #cyan
                    r8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Note("d'8"), Rest('r8')])
                Selection([Note("e'8"), Rest('r8'), Note("f'8")])
                Selection([Note("g'8"), Note("a'8"), Rest('r8')])

        Returns new expression.
        '''
        import abjad
        callback = abjad.PartitionByRatioCallback(ratio)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def print(self, result):
        r'''Prints `result`.

        Returns none.
        '''
        import abjad
        if isinstance(self.callbacks[-1], abjad.GetItemCallback):
            print(repr(result))
        else:
            for item in result:
                print(repr(item))

    @staticmethod
    def run_selectors(argument, selectors, rotation=None):
        r'''Processes multiple selectors against a single selection.

        ..  container:: example

            Minimizes reselection when selectors share identical prefixes of
            selector callbacks:

            ::

                >>> staff = abjad.Staff("c'4 d'8 e'8 f'4 g'8 a'4 b'8 c'8")

            ::

                >>> selector = abjad.select()
                >>> logical_tie_selector = selector.by_logical_tie()
                >>> pitched_selector = logical_tie_selector.by_pitch('C4')
                >>> duration_selector = logical_tie_selector.by_duration('==', (1, 8))
                >>> contiguity_selector = duration_selector.by_contiguity()
                >>> selectors = [
                ...     selector,
                ...     logical_tie_selector,
                ...     pitched_selector,
                ...     duration_selector,
                ...     contiguity_selector,
                ...     ]

            ::

                >>> result = abjad.Selector.run_selectors(staff, selectors)
                >>> all(selector in result for selector in selectors)
                True

            ::

                >>> for item in result[selector]:
                ...     item
                ...
                Staff("c'4 d'8 e'8 f'4 g'8 a'4 b'8 c'8")

            ::

                >>> for item in result[logical_tie_selector]:
                ...     item
                ...
                LogicalTie([Note("c'4")])
                LogicalTie([Note("d'8")])
                LogicalTie([Note("e'8")])
                LogicalTie([Note("f'4")])
                LogicalTie([Note("g'8")])
                LogicalTie([Note("a'4")])
                LogicalTie([Note("b'8")])
                LogicalTie([Note("c'8")])

            ::

                >>> for item in result[pitched_selector]:
                ...     item
                ...
                LogicalTie([Note("c'4")])
                LogicalTie([Note("c'8")])

            ::

                >>> for item in result[duration_selector]:
                ...     item
                ...
                LogicalTie([Note("d'8")])
                LogicalTie([Note("e'8")])
                LogicalTie([Note("g'8")])
                LogicalTie([Note("b'8")])
                LogicalTie([Note("c'8")])

            ::

                >>> for item in result[contiguity_selector]:
                ...     item
                ...
                Selection([LogicalTie([Note("d'8")]), LogicalTie([Note("e'8")])])
                Selection([LogicalTie([Note("g'8")])])
                Selection([LogicalTie([Note("b'8")]), LogicalTie([Note("c'8")])])

        Returns a dictionary of selector/selection pairs.
        '''
        import abjad
        if rotation is None:
            rotation = 0
        rotation = int(rotation)
        prototype = (abjad.Component, abjad.Selection)
        if not isinstance(argument, prototype):
            argument = abjad.select(argument)
        argument = (argument,)
        assert all(isinstance(_, prototype) for _ in argument), repr(argument)
        maximum_length = 0
        for selector in selectors:
            if selector.callbacks:
                maximum_length = max(maximum_length, len(selector.callbacks))
        #print('MAX LENGTH', maximum_length)
        selectors = list(selectors)
        results_by_prefix = {(): argument}
        results_by_selector = collections.OrderedDict()
        for index in range(1, maximum_length + 2):
            #print('INDEX', index)
            #print('PRUNING')
            for selector in selectors[:]:
                callbacks = selector.callbacks or ()
                callback_length = index - 1
                if len(callbacks) == callback_length:
                    prefix = callbacks[:callback_length]
                    results_by_selector[selector] = results_by_prefix[prefix]
                    selectors.remove(selector)
                    #print('\tREMOVED:', selector)
                    #print('\tREMAINING:', len(selectors))
            if not selectors:
                #print('BREAKING')
                break
            #print('ADDING')
            for selector in selectors:
                callbacks = selector.callbacks or ()
                this_prefix = callbacks[:index]
                if this_prefix in results_by_prefix:
                    #print('\tSKIPPING', repr(selector))
                    continue
                #print('\tADDING', repr(selector))
                previous_prefix = callbacks[:index - 1]
                previous_expr = results_by_prefix[previous_prefix]
                callback = this_prefix[-1]
                argument = callback(
                    previous_expr,
                    rotation=rotation,
                    )
                results_by_prefix[this_prefix] = argument
        return results_by_selector

    def with_next_leaf(self):
        r'''Selects with next leaf.

        ..  container:: example

            Selects note runs (with next leaf):

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_run(abjad.Note)
                >>> selector = selector.map(abjad.select().with_next_leaf())

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    g'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Rest('r8')])
                Selection([Note("d'8"), Note("e'8"), Rest('r8')])
                Selection([Note("f'8"), Note("g'8"), Note("a'8")])

        ..  container:: example

            Selects pitched tails (with next leaf):

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> get = abjad.select()[-1].with_next_leaf()
                >>> selector = selector.map(get)

            ::

                >>> staff = abjad.Staff(r"c'8 r d' ~ d' e' ~ e' r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    d'8 ~
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Rest('r8')])
                Selection([Note("d'8"), Note("e'8")])
                Selection([Note("e'8"), Rest('r8')])
                Selection([Note("f'8")])

        ..  container:: example

            Selects pitched logical ties (with next leaf). This is the correct
            selection for single-pitch sustain pedal application:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> selector = selector.map(abjad.select().with_next_leaf())

            ::

                >>> staff = abjad.Staff(r"c'8 r d' ~ d' e' ~ e' r8 f'8")
                >>> result = selector(staff)
                >>> for selection in result:
                ...     abjad.attach(abjad.PianoPedalSpanner(), selection)
                ...

            ::

                >>> selector.color(result)
                >>> manager = abjad.override(staff).sustain_pedal_line_spanner
                >>> manager.staff_padding = 6
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    \override SustainPedalLineSpanner.staff-padding = #6
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    \set Staff.pedalSustainStyle = #'mixed
                    c'8 \sustainOn
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8 \sustainOff
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    \set Staff.pedalSustainStyle = #'mixed
                    d'8 ~ \sustainOn
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    \set Staff.pedalSustainStyle = #'mixed
                    e'8 ~ \sustainOff \sustainOn
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8 \sustainOff
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    \set Staff.pedalSustainStyle = #'mixed
                    f'8 \sustainOn \sustainOff
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8"), Rest('r8')])
                Selection([Note("d'8"), Note("d'8"), Note("e'8")])
                Selection([Note("e'8"), Note("e'8"), Rest('r8')])
                Selection([Note("f'8")])

        Returns new expression.
        '''
        import abjad
        callback = abjad.WithLeafCallback(with_next_leaf=True)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def with_previous_leaf(self):
        r'''Selects with previous leaf.

        ..  container:: example

            Selects note runs (with previous leaf):

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_run(abjad.Note)
                >>> get = abjad.select().with_previous_leaf()
                >>> selector = selector.map(get)

            ::

                >>> staff = abjad.Staff("c'8 r8 d'8 e'8 r8 f'8 g'8 a'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    e'8
                    \once \override Dots.color = #red
                    \once \override Rest.color = #red
                    r8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    f'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    g'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    a'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8")])
                Selection([Rest('r8'), Note("d'8"), Note("e'8")])
                Selection([Rest('r8'), Note("f'8"), Note("g'8"), Note("a'8")])

        ..  container:: example

            Selects pitched heads (with previous leaf):

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_logical_tie(pitched=True)
                >>> get = abjad.select()[0].with_previous_leaf()
                >>> selector = selector.map(get)

            ::

                >>> staff = abjad.Staff(r"c'8 r d' ~ d' e' ~ e' r8 f'8")
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    c'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    d'8 ~
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    d'8
                    \once \override Accidental.color = #red
                    \once \override Beam.color = #red
                    \once \override Dots.color = #red
                    \once \override NoteHead.color = #red
                    \once \override Stem.color = #red
                    e'8 ~
                    e'8
                    \once \override Dots.color = #blue
                    \once \override Rest.color = #blue
                    r8
                    \once \override Accidental.color = #blue
                    \once \override Beam.color = #blue
                    \once \override Dots.color = #blue
                    \once \override NoteHead.color = #blue
                    \once \override Stem.color = #blue
                    f'8
                }

            ::

                >>> selector.print(result)
                Selection([Note("c'8")])
                Selection([Rest('r8'), Note("d'8")])
                Selection([Note("d'8"), Note("e'8")])
                Selection([Rest('r8'), Note("f'8")])

        Returns new expression.
        '''
        import abjad
        callback = abjad.WithLeafCallback(with_previous_leaf=True)
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    def wrap(self):
        r'''Wraps result in list.

        ..  container:: example

            Wraps tuplets:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(abjad.Tuplet)

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { r8 d' e' } f' r
                ...     r f' \times 2/3 { e' d' r8 }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        \once \override Dots.color = #red
                        \once \override Rest.color = #red
                        r8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                    }
                    f'8
                    r8
                    r8
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        \once \override Dots.color = #blue
                        \once \override Rest.color = #blue
                        r8
                    }
                }

            ::

                >>> selector.print(result)
                Tuplet(Multiplier(2, 3), "r8 d'8 e'8")
                Tuplet(Multiplier(2, 3), "e'8 d'8 r8")

        ..  container:: example

            Wraps each tuplet:

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_class(abjad.Tuplet)
                >>> selector = selector.map(abjad.select().wrap())

            ::

                >>> staff = abjad.Staff(r"""
                ...     \times 2/3 { r8 d' e' } f' r
                ...     r f' \times 2/3 { e' d' r8 }
                ...     """)
                >>> result = selector(staff)
                >>> selector.color(result)
                >>> abjad.setting(staff).auto_beaming = False
                >>> show(staff) # doctest: +SKIP

            ..  docs::

                >>> f(staff)
                \new Staff \with {
                    autoBeaming = ##f
                } {
                    \times 2/3 {
                        \once \override Dots.color = #red
                        \once \override Rest.color = #red
                        r8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        d'8
                        \once \override Accidental.color = #red
                        \once \override Beam.color = #red
                        \once \override Dots.color = #red
                        \once \override NoteHead.color = #red
                        \once \override Stem.color = #red
                        e'8
                    }
                    f'8
                    r8
                    r8
                    f'8
                    \times 2/3 {
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        e'8
                        \once \override Accidental.color = #blue
                        \once \override Beam.color = #blue
                        \once \override Dots.color = #blue
                        \once \override NoteHead.color = #blue
                        \once \override Stem.color = #blue
                        d'8
                        \once \override Dots.color = #blue
                        \once \override Rest.color = #blue
                        r8
                    }
                }

            ::

                >>> selector.print(result)
                [Tuplet(Multiplier(2, 3), "r8 d'8 e'8")]
                [Tuplet(Multiplier(2, 3), "e'8 d'8 r8")]

        Returns new expression.
        '''
        import abjad
        callback = abjad.WrapCallback()
        selector = self._append_callback(callback)
        template = self._get_template(inspect.currentframe())
        return abjad.new(selector, template=template)

    ### PUBLIC PROPERTIES ###

    @property
    def callbacks(self):
        r'''Gets callbacks.

        Returns tuple.
        '''
        return self._callbacks

    @property
    def template(self):
        r'''Gets template.

        ..  container:: example

            ::

                >>> selector = abjad.select()
                >>> selector = selector.by_leaf()
                >>> selector = selector.by_run(abjad.Note)
                >>> template = 'select_note_runs()'
                >>> selector = abjad.new(selector, template=template)

            ::

                >>> selector
                select_note_runs()

            ::

                >>> abjad.f(selector)
                select_note_runs()

        Returns string or none.
        '''
        return self._template
