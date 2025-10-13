class Booking {
  final String? id;
  final String? userId;
  final String? serviceId;
  final String? providerId;
  final String? serviceName;
  final String? providerName;
  final String? customerName;
  final String? customerPhone;
  final DateTime? scheduledDate;
  final String? scheduledTime;
  final int? duration;
  final double? totalAmount;
  final String? status;
  final String? address;
  final String? notes;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const Booking({
    this.id,
    this.userId,
    this.serviceId,
    this.providerId,
    this.serviceName,
    this.providerName,
    this.customerName,
    this.customerPhone,
    this.scheduledDate,
    this.scheduledTime,
    this.duration,
    this.totalAmount,
    this.status,
    this.address,
    this.notes,
    this.createdAt,
    this.updatedAt,
  });

  factory Booking.fromJson(Map<String, dynamic> json) {
    return Booking(
      id: json['id']?.toString(),
      userId: json['userId']?.toString(),
      serviceId: json['serviceId']?.toString(),
      providerId: json['providerId']?.toString(),
      serviceName: json['serviceName'],
      providerName: json['providerName'],
      customerName: json['customerName'],
      customerPhone: json['customerPhone'],
      scheduledDate: json['scheduledDate'] != null 
          ? DateTime.tryParse(json['scheduledDate']) 
          : null,
      scheduledTime: json['scheduledTime'],
      duration: json['duration'],
      totalAmount: json['totalAmount']?.toDouble(),
      status: json['status'],
      address: json['address'],
      notes: json['notes'],
      createdAt: json['createdAt'] != null 
          ? DateTime.tryParse(json['createdAt']) 
          : null,
      updatedAt: json['updatedAt'] != null 
          ? DateTime.tryParse(json['updatedAt']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'serviceId': serviceId,
      'providerId': providerId,
      'serviceName': serviceName,
      'providerName': providerName,
      'customerName': customerName,
      'customerPhone': customerPhone,
      'scheduledDate': scheduledDate?.toIso8601String(),
      'scheduledTime': scheduledTime,
      'duration': duration,
      'totalAmount': totalAmount,
      'status': status,
      'address': address,
      'notes': notes,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  Booking copyWith({
    String? id,
    String? userId,
    String? serviceId,
    String? providerId,
    String? serviceName,
    String? providerName,
    String? customerName,
    String? customerPhone,
    DateTime? scheduledDate,
    String? scheduledTime,
    int? duration,
    double? totalAmount,
    String? status,
    String? address,
    String? notes,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return Booking(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      serviceId: serviceId ?? this.serviceId,
      providerId: providerId ?? this.providerId,
      serviceName: serviceName ?? this.serviceName,
      providerName: providerName ?? this.providerName,
      customerName: customerName ?? this.customerName,
      customerPhone: customerPhone ?? this.customerPhone,
      scheduledDate: scheduledDate ?? this.scheduledDate,
      scheduledTime: scheduledTime ?? this.scheduledTime,
      duration: duration ?? this.duration,
      totalAmount: totalAmount ?? this.totalAmount,
      status: status ?? this.status,
      address: address ?? this.address,
      notes: notes ?? this.notes,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  @override
  String toString() {
    return 'Booking(id: $id, serviceName: $serviceName, status: $status, scheduledDate: $scheduledDate)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is Booking && other.id == id;
  }

  @override
  int get hashCode => id.hashCode;
}
