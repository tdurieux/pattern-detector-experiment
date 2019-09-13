import itertools


def test_parametrization_labels(suite, suite_test):
    param = suite_test.add_parameter()
    param.add_labels()

    @suite_test.append_body
    def __code__(): # pylint: disable=unused-variable
        # pylint: disable=undefined-variablepylint: disable=no-member, protected-access, undefined-variable
        slash.context.result.data['params'] = locals().copy()
        slash.context.result.data['params'].pop('self', None)

    res = suite.run()

    for label, value, result in itertools.zip_longest(
            param.labels, param.values, res.get_all_results_for_test(suite_test)):
        assert label
        assert value
        assert result
        #assert result.test_metadata.variation.id[param.name] == label
        assert result.data['params'] == {param.name: value}
