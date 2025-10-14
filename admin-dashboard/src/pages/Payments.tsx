import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  DollarSign, 
  Search,
  Filter,
  MoreVertical,
  Eye,
  RefreshCw,
  Download,
  TrendingUp,
  TrendingDown,
  CheckCircle,
  XCircle,
  Clock,
  AlertCircle,
  CreditCard,
  Banknote
} from 'lucide-react';

import { getPayments, getPaymentStats, updatePaymentStatus, refundPayment } from '../services/api';
import { Payment } from '../services/api';

const Payments: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [methodFilter, setMethodFilter] = useState<string>('ALL');
  const [selectedPayment, setSelectedPayment] = useState<Payment | null>(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [showRefundModal, setShowRefundModal] = useState(false);
  
  const queryClient = useQueryClient();

  // Fetch payments
  const { data: paymentsData, isLoading } = useQuery(
    ['payments', statusFilter, methodFilter],
    () => getPayments(0, 50, statusFilter === 'ALL' ? undefined : statusFilter as any, methodFilter === 'ALL' ? undefined : methodFilter as any),
    {
      refetchInterval: 30000, // Refetch every 30 seconds
    }
  );

  // Fetch payment statistics
  const { data: paymentStats } = useQuery(
    'payment-stats',
    getPaymentStats,
  );

  // Update payment status mutation
  const updateStatusMutation = useMutation(
    ({ id, status }: { id: string; status: string }) => 
      updatePaymentStatus(id, status as any),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['payments']);
      },
    }
  );

  // Refund payment mutation
  const refundMutation = useMutation(
    ({ id, amount, reason }: { id: string; amount: number; reason: string }) => 
      refundPayment(id, amount, reason),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['payments']);
        setShowRefundModal(false);
        setSelectedPayment(null);
      },
    }
  );

  const payments = paymentsData?.content || [];
  const filteredPayments = payments.filter(payment =>
    payment.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
    payment.booking?.user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    payment.booking?.service.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'FAILED': return 'bg-red-100 text-red-800';
      case 'REFUNDED': return 'bg-blue-100 text-blue-800';
      case 'PARTIALLY_REFUNDED': return 'bg-purple-100 text-purple-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING': return <Clock className="w-4 h-4" />;
      case 'COMPLETED': return <CheckCircle className="w-4 h-4" />;
      case 'FAILED': return <XCircle className="w-4 h-4" />;
      case 'REFUNDED': return <RefreshCw className="w-4 h-4" />;
      case 'PARTIALLY_REFUNDED': return <AlertCircle className="w-4 h-4" />;
      default: return <Clock className="w-4 h-4" />;
    }
  };

  const getMethodIcon = (method: string) => {
    switch (method) {
      case 'CARD': return <CreditCard className="w-4 h-4" />;
      case 'UPI': return <Banknote className="w-4 h-4" />;
      case 'NET_BANKING': return <Banknote className="w-4 h-4" />;
      case 'WALLET': return <Banknote className="w-4 h-4" />;
      default: return <DollarSign className="w-4 h-4" />;
    }
  };

  const handleViewDetails = (payment: Payment) => {
    setSelectedPayment(payment);
    setShowDetailsModal(true);
  };

  const handleRefund = (payment: Payment) => {
    setSelectedPayment(payment);
    setShowRefundModal(true);
  };

  const handleStatusUpdate = (paymentId: string, newStatus: string) => {
    updateStatusMutation.mutate({ id: paymentId, status: newStatus });
  };

  const stats = [
    {
      title: 'Total Revenue',
      value: `₹${paymentStats?.totalRevenue || 0}`,
      change: paymentStats?.revenueChange || 0,
      trend: paymentStats?.revenueTrend || 'up',
      icon: DollarSign,
      color: 'green',
    },
    {
      title: 'Successful Payments',
      value: paymentStats?.successfulPayments || 0,
      change: paymentStats?.successfulChange || 0,
      trend: paymentStats?.successfulTrend || 'up',
      icon: CheckCircle,
      color: 'blue',
    },
    {
      title: 'Failed Payments',
      value: paymentStats?.failedPayments || 0,
      change: paymentStats?.failedChange || 0,
      trend: paymentStats?.failedTrend || 'down',
      icon: XCircle,
      color: 'red',
    },
    {
      title: 'Refunded Amount',
      value: `₹${paymentStats?.refundedAmount || 0}`,
      change: paymentStats?.refundedChange || 0,
      trend: paymentStats?.refundedTrend || 'down',
      icon: RefreshCw,
      color: 'purple',
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Payment Management</h1>
          <p className="text-gray-600">Monitor and manage payment transactions</p>
        </div>
        <div className="flex items-center space-x-3">
          <button className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 flex items-center gap-2">
            <RefreshCw className="w-4 h-4" />
            Refresh
          </button>
          <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2">
            <Download className="w-4 h-4" />
            Export
          </button>
        </div>
      </div>

      {/* Payment Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => (
          <div key={index} className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
              </div>
              <div className={`p-3 rounded-full bg-${stat.color}-100`}>
                <stat.icon className={`w-6 h-6 text-${stat.color}-600`} />
              </div>
            </div>
            <div className="mt-4 flex items-center">
              {stat.trend === 'up' ? (
                <TrendingUp className="w-4 h-4 text-green-500 mr-1" />
              ) : (
                <TrendingDown className="w-4 h-4 text-red-500 mr-1" />
              )}
              <span className={`text-sm font-medium ${
                stat.trend === 'up' ? 'text-green-600' : 'text-red-600'
              }`}>
                {stat.change > 0 ? '+' : ''}{stat.change}%
              </span>
              <span className="text-sm text-gray-500 ml-2">vs previous period</span>
            </div>
          </div>
        ))}
      </div>

      {/* Filters and Search */}
      <div className="bg-white p-6 rounded-lg shadow-sm border">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search payments by ID, customer, or service..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
          <div className="flex gap-2">
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Status</option>
              <option value="PENDING">Pending</option>
              <option value="COMPLETED">Completed</option>
              <option value="FAILED">Failed</option>
              <option value="REFUNDED">Refunded</option>
              <option value="PARTIALLY_REFUNDED">Partially Refunded</option>
            </select>
            <select
              value={methodFilter}
              onChange={(e) => setMethodFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Methods</option>
              <option value="CARD">Card</option>
              <option value="UPI">UPI</option>
              <option value="NET_BANKING">Net Banking</option>
              <option value="WALLET">Wallet</option>
            </select>
            <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2">
              <Filter className="w-4 h-4" />
              Filters
            </button>
          </div>
        </div>
      </div>

      {/* Payments List */}
      <div className="bg-white rounded-lg shadow-sm border">
        {isLoading ? (
          <div className="p-8 text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p className="mt-2 text-gray-600">Loading payments...</p>
          </div>
        ) : filteredPayments.length === 0 ? (
          <div className="p-8 text-center">
            <DollarSign className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">No payments found</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Payment
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Customer
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Service
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Amount
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Method
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Date
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredPayments.map((payment) => (
                  <tr key={payment.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          #{payment.id.slice(0, 8)}
                        </div>
                        <div className="text-sm text-gray-500">
                          {payment.gatewayTransactionId || 'N/A'}
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          {payment.booking?.user.name || 'N/A'}
                        </div>
                        <div className="text-sm text-gray-500">
                          {payment.booking?.user.email || 'N/A'}
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">
                        {payment.booking?.service.name || 'N/A'}
                      </div>
                      <div className="text-sm text-gray-500">
                        {payment.booking?.service.category.name || 'N/A'}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">
                        ₹{payment.amount}
                      </div>
                      {payment.refundedAmount > 0 && (
                        <div className="text-sm text-red-600">
                          Refunded: ₹{payment.refundedAmount}
                        </div>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        {getMethodIcon(payment.paymentMethod)}
                        <span className="ml-2 text-sm text-gray-900">{payment.paymentMethod}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(payment.status)}`}>
                        {getStatusIcon(payment.status)}
                        <span className="ml-1">{payment.status}</span>
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {new Date(payment.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleViewDetails(payment)}
                          className="text-blue-600 hover:text-blue-900"
                          title="View Details"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        {payment.status === 'COMPLETED' && payment.refundedAmount < payment.amount && (
                          <button
                            onClick={() => handleRefund(payment)}
                            className="text-orange-600 hover:text-orange-900"
                            title="Refund Payment"
                          >
                            <RefreshCw className="w-4 h-4" />
                          </button>
                        )}
                        {payment.status === 'PENDING' && (
                          <button
                            onClick={() => handleStatusUpdate(payment.id, 'COMPLETED')}
                            className="text-green-600 hover:text-green-900"
                            title="Mark as Completed"
                          >
                            <CheckCircle className="w-4 h-4" />
                          </button>
                        )}
                        <button className="text-gray-400 hover:text-gray-600">
                          <MoreVertical className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Payment Details Modal */}
      {showDetailsModal && selectedPayment && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Payment Details
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Payment ID</label>
                  <p className="text-sm text-gray-900 font-mono">{selectedPayment.id}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Amount</label>
                  <p className="text-sm text-gray-900">₹{selectedPayment.amount}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Payment Method</label>
                  <p className="text-sm text-gray-900">{selectedPayment.paymentMethod}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Status</label>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(selectedPayment.status)}`}>
                    {getStatusIcon(selectedPayment.status)}
                    <span className="ml-1">{selectedPayment.status}</span>
                  </span>
                </div>
              </div>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Gateway Transaction ID</label>
                  <p className="text-sm text-gray-900">{selectedPayment.gatewayTransactionId || 'N/A'}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Refunded Amount</label>
                  <p className="text-sm text-gray-900">₹{selectedPayment.refundedAmount}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Created At</label>
                  <p className="text-sm text-gray-900">{new Date(selectedPayment.createdAt).toLocaleString()}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Updated At</label>
                  <p className="text-sm text-gray-900">{new Date(selectedPayment.updatedAt).toLocaleString()}</p>
                </div>
              </div>
            </div>
            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setShowDetailsModal(false)}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Refund Modal */}
      {showRefundModal && selectedPayment && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Refund Payment
            </h3>
            <p className="text-sm text-gray-600 mb-4">
              Payment ID: {selectedPayment.id.slice(0, 8)}<br />
              Original Amount: ₹{selectedPayment.amount}<br />
              Already Refunded: ₹{selectedPayment.refundedAmount}
            </p>
            <form 
              onSubmit={(e) => {
                e.preventDefault();
                const formData = new FormData(e.currentTarget);
                const amount = parseFloat(formData.get('amount') as string);
                const reason = formData.get('reason') as string;
                refundMutation.mutate({ id: selectedPayment.id, amount, reason });
              }}
              className="space-y-4"
            >
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Refund Amount (₹)
                </label>
                <input
                  type="number"
                  name="amount"
                  required
                  max={selectedPayment.amount - selectedPayment.refundedAmount}
                  min="0.01"
                  step="0.01"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter refund amount"
                />
                <p className="text-sm text-gray-500 mt-1">
                  Maximum: ₹{selectedPayment.amount - selectedPayment.refundedAmount}
                </p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Refund Reason
                </label>
                <textarea
                  name="reason"
                  required
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter reason for refund"
                />
              </div>
              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setShowRefundModal(false)}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={refundMutation.isLoading}
                  className="px-4 py-2 text-sm font-medium text-white bg-orange-600 rounded-md hover:bg-orange-700 disabled:opacity-50"
                >
                  {refundMutation.isLoading ? 'Processing...' : 'Process Refund'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Payments;
