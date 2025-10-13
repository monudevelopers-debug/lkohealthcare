import 'package:flutter/material.dart';

class ServiceCard extends StatelessWidget {
  final dynamic service;
  final VoidCallback? onTap;
  final bool showPrice;
  final bool showDuration;

  const ServiceCard({
    super.key,
    required this.service,
    this.onTap,
    this.showPrice = true,
    this.showDuration = true,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              // Service Icon
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: _getServiceColor(service.category).withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(
                  _getServiceIcon(service.category),
                  color: _getServiceColor(service.category),
                  size: 24,
                ),
              ),
              const SizedBox(width: 16),
              
              // Service Details
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      service.name ?? 'Service',
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      service.description ?? 'No description available',
                      style: TextStyle(
                        fontSize: 14,
                        color: Colors.grey[600],
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        if (showPrice) ...[
                          Icon(
                            Icons.attach_money,
                            size: 16,
                            color: Colors.green[600],
                          ),
                          const SizedBox(width: 4),
                          Text(
                            '₹${service.price ?? '0'}/${service.duration ?? 'session'}',
                            style: TextStyle(
                              fontSize: 14,
                              fontWeight: FontWeight.bold,
                              color: Colors.green[600],
                            ),
                          ),
                        ],
                        if (showDuration && service.duration != null) ...[
                          const SizedBox(width: 16),
                          Icon(
                            Icons.access_time,
                            size: 16,
                            color: Colors.grey[600],
                          ),
                          const SizedBox(width: 4),
                          Text(
                            '${service.duration} hours',
                            style: TextStyle(
                              fontSize: 14,
                              color: Colors.grey[600],
                            ),
                          ),
                        ],
                      ],
                    ),
                  ],
                ),
              ),
              
              // Action Button
              if (onTap != null)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  decoration: BoxDecoration(
                    color: Theme.of(context).primaryColor,
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: const Text(
                    'Book',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }

  Color _getServiceColor(String? category) {
    switch (category?.toUpperCase()) {
      case 'NURSING':
        return Colors.blue;
      case 'ELDERLY':
        return Colors.orange;
      case 'PHYSIOTHERAPY':
        return Colors.green;
      case 'CHILD_CARE':
        return Colors.purple;
      case 'AMBULANCE':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  IconData _getServiceIcon(String? category) {
    switch (category?.toUpperCase()) {
      case 'NURSING':
        return Icons.health_and_safety;
      case 'ELDERLY':
        return Icons.elderly;
      case 'PHYSIOTHERAPY':
        return Icons.accessibility;
      case 'CHILD_CARE':
        return Icons.child_care;
      case 'AMBULANCE':
        return Icons.local_hospital;
      default:
        return Icons.medical_services;
    }
  }
}
