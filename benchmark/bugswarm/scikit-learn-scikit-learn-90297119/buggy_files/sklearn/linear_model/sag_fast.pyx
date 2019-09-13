# cython: cdivision=True
# cython: boundscheck=False
# cython: wraparound=False
#
# Authors: Danny Sullivan <dbsullivan23@gmail.com>
#          Tom Dupre la Tour <tom.dupre-la-tour@m4x.org>
#
# Licence: BSD 3 clause
import numpy as np
cimport numpy as np
import scipy.sparse as sp
from libc.math cimport fabs, exp, log
from libc.time cimport time, time_t

from ..utils.seq_dataset cimport SequentialDataset
from .sgd_fast cimport LossFunction
from .sgd_fast cimport Log, SquaredLoss


cdef extern from "sgd_fast_helpers.h":
    bint skl_isfinite(double) nogil


cdef inline double fmax(double x, double y) nogil:
    if x > y:
        return x
    return y


cdef double _logsumexp(double* arr, int n_classes) nogil:
    """Computes the sum of arr assuming arr is in the log domain.

    Returns log(sum(exp(arr))) while minimizing the possibility of
    over/underflow.
    """
    # Use the max to normalize, as with the log this is what accumulates
    # the less errors
    cdef double vmax = arr[0]
    cdef double out = 0.0
    cdef int i

    for i in range(1, n_classes):
        if vmax < arr[i]:
            vmax = arr[i]

    for i in range(n_classes):
        out += exp(arr[i] - vmax)

    return log(out) + vmax


cdef class MultinomialLogLoss:
    cdef double _loss(self, double* prediction, double y, int n_classes,
                      double sample_weight) nogil:
        """Multinomial Logistic regression loss.

        The multinomial logistic loss is equal to:
        loss = - sw \sum_c \delta_{y,c} (prediction[c] - logsumexp(prediction))
             = sw (logsumexp(prediction) - prediction[y])

        where:
            prediction = dot(x_sample, weights) + intercept
            \delta_{y,c} = 1 if (y == c) else 0
            sw = sample_weight

        Parameters
        ----------
        prediction : pointer to a np.ndarray[double] of shape (n_classes,)
            Prediction of the multinomial classifier, for current sample.

        y : double, between 0 and n_classes - 1
            Indice of the correct class for current sample.

        n_classes : integer
            Total number of classes.

        sample_weight : double
            Weight of current sample.

        Returns
        -------
        loss : double
            Multinomial loss for current sample.

        Reference
        ---------
        Bishop, C. M. (2006). Pattern recognition and machine learning.
        Springer. (Chapter 4.3.4)
        """
        cdef double logsumexp_prediction = _logsumexp(prediction, n_classes)
        cdef double loss

        # y is the indice of the correct class of current sample.
        loss = (logsumexp_prediction - prediction[int(y)]) * sample_weight
        return loss

    cdef void _dloss(self, double* prediction, double y, int n_classes,
                     double sample_weight, double* gradient_ptr) nogil:
        """Multinomial Logistic regression gradient of the loss.

        The gradient of the multinomial logistic loss is equal to:
        grad_c = - sw * (p[c] - \delta_{y,c})

        where:
            p[c] = exp(logsumexp(prediction) - prediction[c])
            prediction = dot(sample, weights) + intercept
            \delta_{y,c} = 1 if (y == c) else 0
            sw = sample_weight

        Note that to obtain the true gradient, this value has to be multiplied
        by the sample vector x.

        Parameters
        ----------
        prediction : pointer to a np.ndarray[double] of shape (n_classes,)
            Prediction of the multinomial classifier, for current sample.

        y : double, between 0 and n_classes - 1
            Indice of the correct class for current sample.

        n_classes : integer
            Total number of classes.

        sample_weight : double
            Weight of current sample.

        gradient_ptr : pointer to a np.ndarray[double] of shape (n_classes,)
            Gradient vector to be filled.

        Reference
        ---------
        Bishop, C. M. (2006). Pattern recognition and machine learning.
        Springer. (Chapter 4.3.4)
        """
        cdef double logsumexp_prediction = _logsumexp(prediction, n_classes)
        cdef int c

        for c in range(n_classes):
            gradient_ptr[c] = exp(prediction[c] - logsumexp_prediction)

        # y is the indice of the correct class of current sample.
        gradient_ptr[int(y)] -= 1.0

        for c in range(n_classes):
            gradient_ptr[c] *= sample_weight

    def __reduce__(self):
        return MultinomialLogLoss, ()


def _multinomial_grad_loss_all_samples(
        SequentialDataset dataset,
        np.ndarray[double, ndim=2, mode='c'] weights_array,
        np.ndarray[double, ndim=1, mode='c'] intercept_array,
        int n_samples, int n_features, int n_classes):
    """Compute multinomial gradient and loss across all samples.

    Used for testing purpose only.
    """
    cdef double* weights = <double * >weights_array.data
    cdef double* intercept = <double * >intercept_array.data

    cdef double *x_data_ptr = NULL
    cdef int *x_ind_ptr = NULL
    cdef int xnnz = -1
    cdef double y
    cdef double sample_weight

    cdef double wscale = 1.0
    cdef int i, c, j, f, sample_index
    cdef double val
    cdef double sum_loss = 0.0

    cdef MultinomialLogLoss multiloss = MultinomialLogLoss()

    cdef np.ndarray[double, ndim=2] sum_gradient_array = \
        np.zeros((n_features, n_classes), dtype=np.double, order="c")
    cdef double* sum_gradient = <double*> sum_gradient_array.data

    cdef np.ndarray[double, ndim=1] prediction_array = \
        np.zeros(n_classes, dtype=np.double, order="c")
    cdef double* prediction = <double*> prediction_array.data

    cdef np.ndarray[double, ndim=1] gradient_array = \
        np.zeros(n_classes, dtype=np.double, order="c")
    cdef double* gradient = <double*> gradient_array.data

    with nogil:
        for i in range(n_samples):
            # get next sample on the dataset
            dataset.next(&x_data_ptr, &x_ind_ptr, &xnnz,
                         &y, &sample_weight)

            # prediction of the multinomial classifier for the sample
            predict_sample(x_data_ptr, x_ind_ptr, xnnz, weights, wscale,
                           intercept, prediction, n_classes)

            # compute the gradient for this sample, given the prediction
            multiloss._dloss(prediction, y, n_classes, sample_weight, gradient)

            # compute the loss for this sample, given the prediction
            sum_loss += multiloss._loss(prediction, y, n_classes, sample_weight)

            # update the sum of the gradient
            for j in range(xnnz):
                f = x_ind_ptr[j]
                val = x_data_ptr[j]
                for c in range(n_classes):
                    sum_gradient[f * n_classes + c] += gradient[c] * val

    return sum_loss, sum_gradient_array


def sag(SequentialDataset dataset,
        np.ndarray[double, ndim=2, mode='c'] weights_array,
        np.ndarray[double, ndim=1, mode='c'] intercept_array,
        int n_samples,
        int n_features,
        int n_classes,
        double tol,
        int max_iter,
        str loss_function,
        double step_size,
        double alpha,
        np.ndarray[double, ndim=2, mode='c'] sum_gradient_init,
        np.ndarray[double, ndim=2, mode='c'] gradient_memory_init,
        np.ndarray[bint, ndim=1, mode='c'] seen_init,
        int num_seen,
        bint fit_intercept,
        np.ndarray[double, ndim=1, mode='c'] intercept_sum_gradient_init,
        double intercept_decay,
        bint verbose):
    """Stochastic Average Gradient (SAG) solver.

    Used in Ridge and LogisticRegression.

    Reference
    ---------
    Schmidt, M., Roux, N. L., & Bach, F. (2013).
    Minimizing finite sums with the stochastic average gradient
    https://hal.inria.fr/hal-00860051/PDF/sag_journal.pdf
    (section 4.3)
    """
    # the data pointer for X, the training set
    cdef double *x_data_ptr = NULL
    # the index pointer for the column of the data
    cdef int *x_ind_ptr = NULL
    # the number of non-zero features for this sample
    cdef int xnnz = -1
    # the label value for curent sample
    cdef double y
    # true if the weights or intercept are NaN or infinity
    cdef bint infinity = False
    # the sample weight
    cdef double sample_weight
    # helper variable for indexes
    cdef int idx, f, c, j
    # the number of pass through all samples
    cdef int n_iter = 0
    # helper to track iterations through samples
    cdef int itr
    # the index (row number) of the current sample
    cdef int sample_index
    # the maximum change in weights, used to compute stopping criterea
    cdef double max_change
    # a holder variable for the max weight, used to compute stopping criterea
    cdef double max_weight
    # the start time of the fit
    cdef time_t start_time
    # the end time of the fit
    cdef time_t end_time
    # precomputation since the step size does not change in this implementation
    cdef double wscale_update = 1.0 - step_size * alpha
    # vector of booleans indicating whether this sample has been seen
    cdef bint* seen = <bint*> seen_init.data
    # helper for cumulative sum
    cdef double cum_sum

    # the pointer to the coef_ or weights
    cdef double* weights = <double * >weights_array.data
    # the pointer to the intercept_array
    cdef double* intercept = <double * >intercept_array.data
    # the pointer to the intercept_sum_gradient
    cdef double* intercept_sum_gradient = \
        <double * >intercept_sum_gradient_init.data
    # the sum of gradients for each feature
    cdef double* sum_gradient = <double*> sum_gradient_init.data
    # the previously seen gradient for each sample
    cdef double* gradient_memory = <double*> gradient_memory_init.data

    # the cumulative sums needed for JIT params
    cdef np.ndarray[double, ndim=1] cumulative_sums_array = \
        np.empty(n_samples, dtype=np.double, order="c")
    cdef double* cumulative_sums = <double*> cumulative_sums_array.data

    # the index for the last time this feature was updated
    cdef np.ndarray[int, ndim=1] feature_hist_array = \
        np.zeros(n_features, dtype=np.int32, order="c")
    cdef int* feature_hist = <int*> feature_hist_array.data

    # the previous weights to use to compute stopping criteria
    cdef np.ndarray[double, ndim=2] previous_weights_array = \
        np.zeros((n_features, n_classes), dtype=np.double, order="c")
    cdef double* previous_weights = <double*> previous_weights_array.data

    cdef np.ndarray[double, ndim=1] prediction_array = \
        np.zeros(n_classes, dtype=np.double, order="c")
    cdef double* prediction = <double*> prediction_array.data

    cdef np.ndarray[double, ndim=1] gradient_array = \
        np.zeros(n_classes, dtype=np.double, order="c")
    cdef double* gradient = <double*> gradient_array.data

    # the scalar used for multiplying z
    cdef double wscale = 1.0

    # the cumulative sums for each iteration for the sparse implementation
    cumulative_sums[0] = 0.0

    # Loss function to optimize
    cdef LossFunction loss
    # Wether the loss function is multinomial
    cdef bint multinomial = False
    # Multinomial loss function
    cdef MultinomialLogLoss multiloss

    if loss_function == "multinomial":
        multinomial = True
        multiloss = MultinomialLogLoss()
    elif loss_function == "log":
        loss = Log()
    elif loss_function == "squared":
        loss = SquaredLoss()
    else:
        raise ValueError("Invalid loss parameter: got %s instead of "
                         "one of ('log', 'squared', 'multinomial')"
                         % loss_function)

    with nogil:
        start_time = time(NULL)
        while True:
            if infinity:
                break
            for itr in range(n_samples):

                # extract a random sample
                sample_index = dataset.random(&x_data_ptr, &x_ind_ptr, &xnnz,
                                              &y, &sample_weight)

                # update the number of samples seen and the seen array
                if seen[sample_index] == 0:
                    num_seen += 1
                    seen[sample_index] = 1

                # make the weight updates
                if itr > 0:
                    for j in range(xnnz):
                        f = x_ind_ptr[j]
                        idx = f * n_classes - 1

                        cum_sum = cumulative_sums[itr - 1]
                        if feature_hist[f] != 0:
                            cum_sum -= cumulative_sums[feature_hist[f] - 1]

                        for c in range(n_classes):
                            idx += 1  # idx = f * n_classes + c
                            weights[idx] -= cum_sum * sum_gradient[idx]

                            # check to see that the weight is not inf or NaN
                            if not skl_isfinite(weights[idx]):
                                infinity = True
                                break

                        feature_hist[f] = itr

                if infinity:
                    break

                # find the current prediction
                predict_sample(x_data_ptr, x_ind_ptr, xnnz, weights, wscale,
                               intercept, prediction, n_classes)

                # compute the gradient for this sample, given the prediction
                if multinomial:
                    multiloss._dloss(prediction, y, n_classes, sample_weight,
                                     gradient)
                else:
                    gradient[0] = loss._dloss(prediction[0], y) * sample_weight

                # make the updates to the sum of gradients
                for j in range(xnnz):
                    f = x_ind_ptr[j]
                    val = x_data_ptr[j]
                    for c in range(n_classes):
                        sum_gradient[f * n_classes + c] += \
                            (gradient[c] -
                             gradient_memory[sample_index * n_classes + c]) * val

                # fit the intercept
                if fit_intercept:
                    for c in range(n_classes):
                        intercept_sum_gradient[c] += \
                            (gradient[c] -
                             gradient_memory[sample_index * n_classes + c])
                        intercept[c] -= (step_size * intercept_sum_gradient[c]
                                         / num_seen * intercept_decay)

                        # check to see that the intercept is not inf or NaN
                        if infinity or not skl_isfinite(intercept[c]):
                            infinity = True
                            break
                if infinity:
                    break

                # update the gradient memory for this sample
                idx = sample_index * n_classes - 1
                for c in range(n_classes):
                    idx += 1  # idx = sample_index * n_classes + c
                    gradient_memory[idx] = gradient[c]

                # L2 regularization by simply rescaling the weights
                wscale *= wscale_update

                if itr == 0:
                    cumulative_sums[0] = step_size / (wscale * num_seen)
                else:
                    cumulative_sums[itr] = (cumulative_sums[itr - 1]
                                            + step_size / (wscale * num_seen))

                # if wscale gets too small, we need to reset the scale
                if wscale < 1e-9:
                    if verbose:
                        with gil:
                            print("rescaling...")
                    scale_weights(weights, wscale, n_features, n_samples,
                                  n_classes, itr, cumulative_sums,
                                  feature_hist, sum_gradient)
                    wscale = 1.0

            # check if the stopping criteria is reached
            scale_weights(weights, wscale, n_features, n_samples, n_classes, 
                          n_samples - 1, cumulative_sums, feature_hist,
                          sum_gradient)
            wscale = 1.0

            max_change = 0.0
            max_weight = 0.0
            for f in range(n_features):
                max_weight = fmax(max_weight, fabs(weights[f]))
            for f in range(n_features):
                max_change = fmax(max_change,
                                  fabs(weights[f] - previous_weights[f]))
                previous_weights[f] = weights[f]

            n_iter += 1
            if max_change / max_weight <= tol:
                if verbose:
                    end_time = time(NULL)
                    with gil:
                        print("convergence after %d epochs took %d seconds" %
                              (n_iter, end_time - start_time))
                break

            if n_iter >= max_iter:
                if verbose:
                    end_time = time(NULL)
                    with gil:
                        print(("max_iter reached after %d seconds") %
                              (end_time - start_time))
                break
    if infinity:
        raise ValueError("Floating-point under-/overflow occurred at "
                         "epoch #%d. Lowering the step_size or "
                         "scaling the input data with StandardScaler "
                         "or MinMaxScaler might help." % (n_iter + 1))

    return num_seen, n_iter


cdef void scale_weights(double* weights, double wscale, int n_features,
                        int n_samples, int n_classes, int itr,
                        double* cumulative_sums, int* feature_hist,
                        double* sum_gradient) nogil:
    cdef int f, c, idx
    cdef double cum_sum
    idx = -1
    for f in range(n_features):
        cum_sum = cumulative_sums[itr]
        if feature_hist[f] != 0:
            cum_sum -= cumulative_sums[feature_hist[f] - 1]

        for c in range(n_classes):
            idx += 1  # idx = f * n_classes + c
            weights[idx] -= cum_sum * sum_gradient[idx]
            weights[idx] *= wscale

        feature_hist[f] = (itr + 1) % n_samples

    cumulative_sums[itr % n_samples] = 0.0


def get_max_squared_sum(X):
    """Maximum squared sum of X over samples. 

    Used in ``get_auto_step_size()``, for SAG solver.

    Parameter
    ---------
    X : {numpy array, scipy CSR sparse matrix}, shape (n_samples, n_features)
        Training vector. X must be in C order.

    Returns
    -------
    max_squared_sum : double
        Maximum squared sum of X over samples.
    """

    # CSR sparse matrix X
    cdef np.ndarray[double] X_data
    cdef np.ndarray[int] X_indptr
    cdef double *X_data_ptr
    cdef int *X_indptr_ptr
    cdef int offset

    # Dense numpy array X
    cdef np.ndarray[double, ndim=2] X_ndarray
    cdef int stride

    # Both cases
    cdef bint sparse = sp.issparse(X)
    cdef double *x_data_ptr
    cdef double max_squared_sum = 0.0
    cdef double current_squared_sum = 0.0
    cdef int n_samples = X.shape[0]
    cdef int nnz = X.shape[1]
    cdef int i, f
    cdef double val

    if sparse:
        X_data = X.data
        X_indptr = X.indptr
        X_data_ptr = <double *>X_data.data
        X_indptr_ptr = <int *>X_indptr.data
    else:
        X_ndarray = X
        stride = X_ndarray.strides[0] / X_ndarray.itemsize
        x_data_ptr = <double *>X_ndarray.data - stride

    with nogil:
        for i in range(n_samples):
            # find next sample data
            if sparse:
                offset = X_indptr_ptr[i]
                nnz = X_indptr_ptr[i + 1] - offset
                x_data_ptr = X_data_ptr + offset
            else:
                x_data_ptr += stride

            # sum of squared non-zero features
            for f in range(nnz):
                val = x_data_ptr[f]
                current_squared_sum += val * val

            if current_squared_sum > max_squared_sum:
                max_squared_sum = current_squared_sum
            current_squared_sum = 0.0


    if not skl_isfinite(max_squared_sum):
        raise ValueError("Floating-point under-/overflow occurred")

    return max_squared_sum


cdef void predict_sample(double* x_data_ptr, int* x_ind_ptr, int xnnz,
                         double* w_data_ptr, double wscale, double* intercept,
                         double* prediction, int n_classes) nogil:
    """Compute the prediction given sparse sample x and dense weight w.

    Parameters
    ----------
    x_data_ptr : pointer
        Pointer to the data of the sample x

    x_ind_ptr : pointer
        Pointer to the indices of the sample  x

    xnnz : int
        Number of non-zero element in the sample  x

    w_data_ptr : pointer
        Pointer to the data of the weights w

    wscale : double
        Scale of the weights w

    intercept : pointer
        Pointer to the intercept

    prediction : pointer
        Pointer to store the resulting prediction

    n_classes : int
        Number of classes in multinomial case. Equals 1 in binary case.

    """

    cdef int f, c, idx
    cdef double innerprod

    for c in range(n_classes):
        innerprod = 0.0
        # Compute the dot product only on non-zero elements of x
        for f in range(xnnz):
            idx = x_ind_ptr[f]
            innerprod += w_data_ptr[idx * n_classes + c] * x_data_ptr[f]

        prediction[c] = wscale * innerprod + intercept[c]
